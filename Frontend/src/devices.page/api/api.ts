import api from "../../common/api/api.ts";
import type {DeviceDTO} from "../../common/types/DeviceDTO.ts";
import {ENDPOINT_DEVICE_SERVICE} from "../../common/Constants.ts"

async function getDevices() {
    return await api.get(`${ENDPOINT_DEVICE_SERVICE}/user/get-devices`)
}

async function createDevice(device: DeviceDTO) {
    return await api.post(`${ENDPOINT_DEVICE_SERVICE}/admin/create-device`, device)
}

async function updateDevice(id: string, device: DeviceDTO) {
    return await api.put(`${ENDPOINT_DEVICE_SERVICE}/admin/update-device/${id}`, device)
}

async function deleteDevice(id: string) {
    return await api.delete(`${ENDPOINT_DEVICE_SERVICE}/admin/delete-device/${id}`)
}

export {getDevices, createDevice, updateDevice, deleteDevice}