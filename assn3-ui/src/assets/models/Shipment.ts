import type { UpdateType } from "./UpdateType";

export interface Shipment {
  id: string;
  timestamp: string;
  otherInfo?: string;
  updateType: UpdateType;
}