import api from "../../common/api/api.ts";

import {ENDPOINT_DEVICE_SERVICE, ENDPOINT_MONITORING_SERVICE} from "../../common/Constants.ts"


async function getUserDevices(id: string) {
    return await api.get(`${ENDPOINT_DEVICE_SERVICE}/user/user-devices/${id}`)
}

async function getHourlyConsumption(deviceIds: string[], date: string) {
    return await api.post(`${ENDPOINT_MONITORING_SERVICE}/user/hourly-consumption/${date}`, deviceIds);
}

export {getUserDevices, getHourlyConsumption};