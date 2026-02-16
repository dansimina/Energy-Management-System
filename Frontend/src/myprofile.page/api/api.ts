import api from "../../common/api/api.ts";

import {ENDPOINT_DEVICE_SERVICE} from "../../common/Constants.ts"


async function getUserDevices(userId: string) {
    return await api.get(`${ENDPOINT_DEVICE_SERVICE}/user/user-devices/${userId}`);
}

export {getUserDevices};