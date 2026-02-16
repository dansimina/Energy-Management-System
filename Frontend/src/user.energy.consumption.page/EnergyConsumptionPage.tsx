import {useState, useEffect} from 'react';
import {
    Box,
    Container,
    Typography,
    Card,
    CardContent,
    TextField,
    CircularProgress,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    ToggleButton,
    ToggleButtonGroup,
} from '@mui/material';
import {LineChart} from '@mui/x-charts/LineChart';
import {BarChart} from '@mui/x-charts/BarChart';
import {useParams} from 'react-router-dom';
import AppNavigationBar from "../common/components/AppNavigationBar";
import {getUserDevices, getHourlyConsumption} from "./api/api.ts";
import type {DeviceDTO} from "../common/types/DeviceDTO.ts";
import type {HourlyConsumptionDTO} from "../common/types/HourlyConsumptionDTO.ts";

function EnergyConsumptionPage() {
    const {userId} = useParams<{ userId: string }>();
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState<HourlyConsumptionDTO[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [chartType, setChartType] = useState<'line' | 'bar'>('line');

    const fetchConsumptionData = async (date: string) => {
        if (!userId) {
            console.error('No userId provided');
            return;
        }

        setLoading(true);
        setError(null);
        try {
            const devicesResponse = await getUserDevices(userId);
            const deviceIds = devicesResponse.data.map((d: DeviceDTO) => d.id);

            if (deviceIds.length === 0) {
                console.warn('No devices found for user');
                setData([]);
                setLoading(false);
                return;
            }

            const consumptionResponse = await getHourlyConsumption(deviceIds, date);
            console.log('Consumption data received:', consumptionResponse.data);

            // Ensure data is an array and not undefined
            if (Array.isArray(consumptionResponse.data)) {
                setData(consumptionResponse.data);
            } else {
                console.error('Data is not an array:', consumptionResponse.data);
                setData([]);
            }
        } catch (err) {
            console.error('Error fetching data:', err);
            setError('Failed to load consumption data');
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchConsumptionData(selectedDate);
    }, [userId]);

    const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newDate = e.target.value;
        setSelectedDate(newDate);
        fetchConsumptionData(newDate);
    };

    const handleChartTypeChange = (_e: React.MouseEvent<HTMLElement>, newChartType: 'line' | 'bar') => {
        if (newChartType !== null) {
            setChartType(newChartType);
        }
    };

    // Safe data processing with guards
    const hours = data && data.length > 0 ? data.map(d => {
        const timeStr = typeof d.time === 'string' ? d.time : '00:00';
        return parseInt(timeStr.split(':')[0]);
    }) : [];

    const consumptions = data && data.length > 0 ? data.map(d => d.value || 0) : [];
    const totalConsumption = consumptions.reduce((sum, val) => sum + (val || 0), 0);
    const averageConsumption = data && data.length > 0 ? (totalConsumption / data.length).toFixed(2) : '0';
    const maxConsumption = consumptions.length > 0 ? Math.max(...consumptions) : 0;

    return (
        <Box sx={{display: 'flex', flexDirection: 'column', minHeight: '100vh'}}>
            <AppNavigationBar/>

            <Container component="main" sx={{flexGrow: 1, py: 4}}>

                {/* Header */}
                <Box sx={{mb: 4}}>
                    <Typography variant="h4" component="h1" sx={{fontWeight: 'bold', mb: 1}}>
                        Energy Consumption
                    </Typography>
                    <Typography variant="body1" color="text.secondary">
                        Monitor your daily energy usage
                    </Typography>
                </Box>

                {/* Controls Section */}
                <Card sx={{mb: 4}}>
                    <CardContent>
                        <Box sx={{display: 'flex', gap: 3, flexWrap: 'wrap', alignItems: 'center'}}>
                            <TextField
                                type="date"
                                value={selectedDate}
                                onChange={handleDateChange}
                                InputLabelProps={{shrink: true}}
                                label="Select Date"
                                variant="outlined"
                                size="small"
                                sx={{flex: 1, minWidth: 250}}
                            />
                        </Box>
                    </CardContent>
                </Card>

                {/* Stats Cards */}
                <Box sx={{display: 'flex', gap: 2, mb: 4, flexWrap: 'wrap'}}>
                    <Card sx={{flex: 1, minWidth: 200}}>
                        <CardContent>
                            <Typography color="text.secondary" gutterBottom
                                        sx={{fontSize: '0.875rem', fontWeight: 600}}>
                                Total Consumption
                            </Typography>
                            <Typography variant="h5" sx={{fontWeight: 'bold', color: '#1976d2'}}>
                                {totalConsumption.toFixed(2)} Wh
                            </Typography>
                        </CardContent>
                    </Card>

                    <Card sx={{flex: 1, minWidth: 200}}>
                        <CardContent>
                            <Typography color="text.secondary" gutterBottom
                                        sx={{fontSize: '0.875rem', fontWeight: 600}}>
                                Average per Hour
                            </Typography>
                            <Typography variant="h5" sx={{fontWeight: 'bold', color: '#7c3aed'}}>
                                {averageConsumption} Wh
                            </Typography>
                        </CardContent>
                    </Card>

                    <Card sx={{flex: 1, minWidth: 200}}>
                        <CardContent>
                            <Typography color="text.secondary" gutterBottom
                                        sx={{fontSize: '0.875rem', fontWeight: 600}}>
                                Peak Consumption
                            </Typography>
                            <Typography variant="h5" sx={{fontWeight: 'bold', color: '#dc2626'}}>
                                {maxConsumption.toFixed(2)} Wh
                            </Typography>
                        </CardContent>
                    </Card>
                </Box>

                {/* Error Message */}
                {error && (
                    <Card sx={{mb: 4, backgroundColor: '#ffebee', borderLeft: '4px solid #dc2626'}}>
                        <CardContent>
                            <Typography color="error">
                                {error}
                            </Typography>
                        </CardContent>
                    </Card>
                )}

                {/* Chart Section */}
                <Card sx={{mb: 4}}>
                    <CardContent>
                        <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2}}>
                            <Typography variant="h6" sx={{fontWeight: 600}}>
                                Energy Usage Chart
                            </Typography>
                            <ToggleButtonGroup
                                value={chartType}
                                exclusive
                                onChange={handleChartTypeChange}
                                aria-label="chart type"
                                size="small"
                            >
                                <ToggleButton value="line" aria-label="line chart">
                                    Line Chart
                                </ToggleButton>
                                <ToggleButton value="bar" aria-label="bar chart">
                                    Bar Chart
                                </ToggleButton>
                            </ToggleButtonGroup>
                        </Box>

                        {loading ? (
                            <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300}}>
                                <CircularProgress/>
                            </Box>
                        ) : data.length === 0 ? (
                            <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300}}>
                                <Typography color="text.secondary">
                                    {error ? 'Error loading data' : 'No data available for the selected date'}
                                </Typography>
                            </Box>
                        ) : chartType === 'line' ? (
                            <LineChart
                                xAxis={[{data: hours, label: 'Hour'}]}
                                series={[
                                    {
                                        data: consumptions,
                                        label: 'Energy (Wh)',
                                    },
                                ]}
                                height={300}
                                margin={{top: 10, bottom: 30, left: 60, right: 10}}
                            />
                        ) : (
                            <BarChart
                                xAxis={[{scaleType: 'band', data: hours.map(h => `${h}:00`), label: 'Hour'}]}
                                series={[
                                    {
                                        data: consumptions,
                                        label: 'Energy (Wh)',
                                    },
                                ]}
                                height={300}
                                margin={{top: 10, bottom: 30, left: 60, right: 10}}
                            />
                        )}
                    </CardContent>
                </Card>

                {/* Table Section */}
                <Card>
                    <CardContent>
                        <Typography variant="h6" sx={{mb: 2, fontWeight: 600}}>
                            Hourly Consumption Details
                        </Typography>
                        {loading ? (
                            <Box sx={{display: 'flex', justifyContent: 'center', py: 3}}>
                                <CircularProgress/>
                            </Box>
                        ) : data.length === 0 ? (
                            <Typography color="text.secondary" sx={{py: 3}}>
                                No data available
                            </Typography>
                        ) : (
                            <TableContainer component={Paper}>
                                <Table>
                                    <TableHead sx={{backgroundColor: '#f5f5f5'}}>
                                        <TableRow>
                                            <TableCell sx={{fontWeight: 'bold'}}>Hour</TableCell>
                                            <TableCell align="right" sx={{fontWeight: 'bold'}}>Consumption
                                                (Wh)</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {data.map((row, index) => (
                                            <TableRow key={index} sx={{'&:hover': {backgroundColor: '#fafafa'}}}>
                                                <TableCell>{row.time}</TableCell>
                                                <TableCell align="right">{(row.value || 0).toFixed(2)}</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        )}
                    </CardContent>
                </Card>
            </Container>
        </Box>
    );
}

export default EnergyConsumptionPage;