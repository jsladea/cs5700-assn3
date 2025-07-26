import type {UpdateType} from '../../assets/models/UpdateType'; // Adjust the path as needed

const convertStringToUpdateType = (type: string): UpdateType => {
  switch (type.toUpperCase()) {
    case "CREATED":
    case "SHIPPED":
    case "DELIVERED":
    case "DELAYED":
    case "LOST":
    case "CANCELED":
      return type as UpdateType;
    case "LOCATION CHANGED":
      return "LOCATION_CHANGED";
    case "ADD NOTE":
      return "NOTE_ADDED";
    default:
      throw new Error(`Unable to convert input to update type. Input: ${type}`);
  }
};

const coverUpdateTypeToBackendString = (type: UpdateType): string => {
  switch (type) {
    case "CREATED":
      return "created";
    case "SHIPPED":
      return "shipped";
    case "LOCATION_CHANGED":
      return "location";
    case "DELIVERED":
      return "delivered";
    case "DELAYED":
      return "delayed";
    case "LOST":
      return "lost";
    case "CANCELED":
      return "canceled";
    case "NOTE_ADDED":
      return "noteadded";
    default:
      throw new Error(`Unknown update type: ${type}`);
  }
};

export {convertStringToUpdateType, coverUpdateTypeToBackendString};