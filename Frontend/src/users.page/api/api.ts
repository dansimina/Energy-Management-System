import api from "../../common/api/api.ts";
import type {RegisterRequestDTO} from "../../common/types/RegisterRequestDTO.ts";

import {ENDPOINT_AUTH_SERVICE, ENDPOINT_USER_SERVICE} from "../../common/Constants.ts"

async function getUsers() {
    return await api.get(`${ENDPOINT_USER_SERVICE}/admin/get-users`)
}

async function createUser(user: RegisterRequestDTO) {
    return await api.post(`${ENDPOINT_AUTH_SERVICE}/admin/create-user`, user)
}

export {getUsers, createUser};