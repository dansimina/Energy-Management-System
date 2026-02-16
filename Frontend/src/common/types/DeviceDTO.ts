export interface DeviceDTO {
    id: string | null;
    name: string;
    maximumConsumptionValue: number;
    energyClass: "A+++" | "A++" | "A+" | "A" | "B" | "C" | "D" | "E" | "F" | "G";
    description: string;
}
