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

export {convertStringToUpdateType};