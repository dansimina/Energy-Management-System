import {useEffect, useState} from 'react';
import {Snackbar, Alert, Button} from '@mui/material';
import {useNavigate} from 'react-router-dom';
import {useWebSocket} from '../useWebSocketContext';
import {PATH_CHAT} from '../../common/Constants';
import type {AppNotification} from '../types/AppNotification';

function NotificationToast() {
    const navigate = useNavigate();
    const {notifications, clearNotification} = useWebSocket();
    const [current, setCurrent] = useState<AppNotification | null>(null);
    const [open, setOpen] = useState(false);
    const [shownIds, setShownIds] = useState<Set<string>>(new Set());

    useEffect(() => {
        const unshown = notifications.find(n => !shownIds.has(n.id));
        if (unshown && !open) {
            setCurrent(unshown);
            setOpen(true);
            setShownIds(prev => new Set(prev).add(unshown.id));
        }
    }, [notifications, open, shownIds]);

    const handleClose = () => {
        setOpen(false);
        setTimeout(() => setCurrent(null), 300);
    };

    const handleDismiss = () => {
        if (current) clearNotification(current.id);
        handleClose();
    };

    const handleOpenChat = () => {
        const sessionId = current?.data?.sessionId;
        if (current) clearNotification(current.id);
        handleClose();
        navigate(sessionId ? `${PATH_CHAT}?session=${sessionId}` : PATH_CHAT);
    };

    const getSeverity = (type: string): 'info' | 'warning' | 'error' | 'success' => {
        switch (type) {
            case 'ALERT':
                return 'error';
            case 'WARNING':
                return 'warning';
            case 'SYSTEM':
                return 'warning'; // System alerts are warnings
            case 'INFO':
                return 'info';
            case 'CHAT':
                return 'info';
            default:
                return 'info';
        }
    };

    if (!current) return null;

    // Both CHAT and SYSTEM notifications can be opened
    const showOpenButton = current.type === 'CHAT' || current.type === 'SYSTEM';

    return (
        <Snackbar
            open={open}
            autoHideDuration={6000}
            onClose={handleClose}
            anchorOrigin={{vertical: 'top', horizontal: 'right'}}
        >
            <Alert
                severity={getSeverity(current.type)}
                variant="filled"
                onClose={handleDismiss}
                action={showOpenButton && (
                    <Button color="inherit" size="small" onClick={handleOpenChat}>
                        Open
                    </Button>
                )}
            >
                <strong>{current.title}</strong><br/>{current.message}
            </Alert>
        </Snackbar>
    );
}

export default NotificationToast;