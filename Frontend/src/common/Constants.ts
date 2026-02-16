const ENDPOINT_USER_SERVICE = "/user-service"
const ENDPOINT_AUTH_SERVICE = "/auth-service"
const ENDPOINT_DEVICE_SERVICE = "/device-service"
const ENDPOINT_MONITORING_SERVICE = "/monitoring-service"

const PATH_HOME = "/"
const PATH_USERS = "/users"
const PATH_USER_DETAIL = "/user/:id"
const PATH_DEVICES = "/devices"
const PATH_PROFILE = "/profile"
const PATH_CONSUMPTION = "/consumption/:userId"
const PATH_CHAT = "/chat"


const getUserDetailsPath = (userId: string | null) => `/user/${userId}`;

const getUserConsumptionPath = (userId: string | null) => `/consumption/${userId}`;

export {
    ENDPOINT_USER_SERVICE,
    ENDPOINT_AUTH_SERVICE,
    ENDPOINT_DEVICE_SERVICE,
    ENDPOINT_MONITORING_SERVICE,
    PATH_HOME,
    PATH_USERS,
    PATH_USER_DETAIL,
    PATH_DEVICES,
    PATH_PROFILE,
    PATH_CONSUMPTION,
    PATH_CHAT,
    getUserDetailsPath,
    getUserConsumptionPath,
};