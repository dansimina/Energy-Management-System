import {useState} from 'react';
import {
    IconButton, Badge, Popover, List, ListItem, ListItemButton,
    ListItemText, Typography, Box, Button, Divider
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import CloseIcon from '@mui/icons-material/Close';
import ChatIcon from '@mui/icons-material/Chat';
import InfoIcon from '@mui/icons-material/Info';
import WarningIcon from '@mui/icons-material/Warning';
import {useNavigate} from 'react-router-dom';
import {useWebSocket} from '../useWebSocketContext';
import {PATH_CHAT} from '../../common/Constants';

function NotificationBell() {
    const navigate = useNavigate();
    const {notifications, unreadCount, clearNotification, clearAllNotifications} = useWebSocket();
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

    const handleNotificationClick = (notification: typeof notifications[0]) => {
        if (notification.type === 'CHAT' || notification.type === 'SYSTEM') {
            const sessionId = notification.data?.sessionId;
            clearNotification(notification.id);
            setAnchorEl(null);
            navigate(sessionId ? `${PATH_CHAT}?session=${sessionId}` : PATH_CHAT);
        }
        // INFO notifications just get dismissed on click
        if (notification.type === 'INFO') {
            clearNotification(notification.id);
        }
    };

    const getIcon = (type: string) => {
        switch (type) {
            case 'CHAT':
                return <ChatIcon fontSize="small" color="primary"/>;
            case 'SYSTEM':
                return <WarningIcon fontSize="small" color="warning"/>;
            case 'INFO':
                return <InfoIcon fontSize="small" color="info"/>;
            default:
                return <InfoIcon fontSize="small"/>;
        }
    };

    return (
        <>
            <IconButton color="inherit" onClick={(e) => setAnchorEl(e.currentTarget)}>
                <Badge badgeContent={unreadCount} color="error">
                    <NotificationsIcon/>
                </Badge>
            </IconButton>

            <Popover
                open={Boolean(anchorEl)}
                anchorEl={anchorEl}
                onClose={() => setAnchorEl(null)}
                anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
                transformOrigin={{vertical: 'top', horizontal: 'right'}}
            >
                <Box sx={{width: 320, maxHeight: 400}}>
                    <Box sx={{p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                        <Typography variant="h6">Notifications</Typography>
                        {notifications.length > 0 && (
                            <Button size="small" onClick={clearAllNotifications}>Clear all</Button>
                        )}
                    </Box>
                    <Divider/>
                    {notifications.length === 0 ? (
                        <Box sx={{p: 3, textAlign: 'center'}}>
                            <Typography color="text.secondary">No notifications</Typography>
                        </Box>
                    ) : (
                        <List sx={{maxHeight: 300, overflow: 'auto'}}>
                            {notifications.map((n) => (
                                <ListItem
                                    key={n.id}
                                    disablePadding
                                    secondaryAction={
                                        <IconButton size="small" onClick={() => clearNotification(n.id)}>
                                            <CloseIcon fontSize="small"/>
                                        </IconButton>
                                    }
                                >
                                    <ListItemButton onClick={() => handleNotificationClick(n)}>
                                        <Box sx={{mr: 1, display: 'flex', alignItems: 'center'}}>
                                            {getIcon(n.type)}
                                        </Box>
                                        <ListItemText
                                            primary={n.title}
                                            secondary={n.message}
                                            primaryTypographyProps={{
                                                variant: 'body2',
                                                fontWeight: 'medium'
                                            }}
                                            secondaryTypographyProps={{
                                                variant: 'caption',
                                                noWrap: true
                                            }}
                                        />
                                    </ListItemButton>
                                </ListItem>
                            ))}
                        </List>
                    )}
                </Box>
            </Popover>
        </>
    );
}

export default NotificationBell;