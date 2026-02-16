import api from "../../common/api/api.ts";
import type {UserDTO} from "../../common/types/UserDTO.ts";
import type {UserCredentialsDTO} from "../../common/types/UserCredentialsDTO.ts";

import {
    ENDPOINT_AUTH_SERVICE,
    ENDPOINT_USER_SERVICE,
    ENDPOINT_DEVICE_SERVICE,
    ENDPOINT_MONITORING_SERVICE
} from "../../common/Constants.ts"

async function getUser(id: string) {
    return await api.get(`${ENDPOINT_USER_SERVICE}/user/get-user/${id}`)
}

async function getUserCredentials(id: string) {
    return await api.get(`${ENDPOINT_AUTH_SERVICE}/admin/user/${id}`)
}

async function getUserDevices(id: string) {
    return await api.get(`${ENDPOINT_DEVICE_SERVICE}/user/user-devices/${id}`)
}

async function getAllDevices() {
    return await api.get(`${ENDPOINT_DEVICE_SERVICE}/user/get-devices`)
}

async function addDeviceToUser(id: string, deviceId: string) {
    return await api.post(`${ENDPOINT_DEVICE_SERVICE}/admin/for-user/${id}/add-device/${deviceId}`);
}

async function removeDeviceFromUser(id: string, deviceId: string) {
    return await api.delete(`${ENDPOINT_DEVICE_SERVICE}/admin/for-user/${id}/remove-device/${deviceId}`);
}

async function updateUser(id: string, user: UserDTO) {
    return await api.put(`${ENDPOINT_USER_SERVICE}/admin/update-user/${id}`, user);
}

async function deleteUser(id: string) {
    return await api.delete(`${ENDPOINT_AUTH_SERVICE}/admin/delete-user/${id}`);
}

async function updateCredentials(id: string, credentials: UserCredentialsDTO) {
    return await api.put(`${ENDPOINT_AUTH_SERVICE}/admin/update-user-credentials/${id}`, credentials);
}

async function getAvailableDevices() {
    return await api.get(`${ENDPOINT_DEVICE_SERVICE}/admin/get-unassigned-devices`)
}

async function getHourlyConsumption(deviceIds: string[], date: string) {
    return await api.post(`${ENDPOINT_MONITORING_SERVICE}/user/hourly-consumption/${date}`, deviceIds);
}

export {
    getUser,
    getUserCredentials,
    getUserDevices,
    getAllDevices,
    getAvailableDevices,
    addDeviceToUser,
    removeDeviceFromUser,
    updateUser,
    deleteUser,
    updateCredentials,
    getHourlyConsumption
};