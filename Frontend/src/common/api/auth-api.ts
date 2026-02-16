import api from "./api";
import type {AuthenticationRequestDTO} from "../types/AuthenticationRequestDTO.ts";
import type {UserDTO} from "../types/UserDTO.ts";
import type {AuthenticationResponseDTO} from "../types/AuthenticationResponseDTO.ts";
import type {RegisterRequestDTO} from "../types/RegisterRequestDTO.ts";
import {ENDPOINT_AUTH_SERVICE, ENDPOINT_USER_SERVICE} from "../Constants.ts"

async function login(request: AuthenticationRequestDTO) {
    return await api.post<AuthenticationResponseDTO>(`${ENDPOINT_AUTH_SERVICE}/login`, request);
}

async function getUser() {
    return await api.get<UserDTO>(`${ENDPOINT_USER_SERVICE}/user/me`);
}

async function register(request: RegisterRequestDTO) {
    return await api.post<AuthenticationResponseDTO>(`${ENDPOINT_AUTH_SERVICE}/register`, request);
}

export {login, register, getUser};