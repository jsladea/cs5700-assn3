import axios from 'axios';
import type { Shipment } from '../../assets/models/Shipment';
import { coverUpdateTypeToBackendString } from './UpdateTypeUtils';

axios.defaults.baseURL = 'http://localhost:8080';

const getShipment = async (shipmentId: string) => {
  try {
    const response = await axios.get(`/shipment/${shipmentId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching shipment:', error);
    throw error;
  }
};

const createShipment = async (shipment: Shipment) => {
  try {
    const requestData = {
      ...shipment,
      updateType: coverUpdateTypeToBackendString(shipment.updateType)
    };
    const response = await axios.post('/shipment/create', requestData);
    return response.data;
  } catch (error) {
    console.error('Error creating shipment:', error);
    throw error;
  }
}

const updateShipment = async (shipment: Shipment) => {
  try {
    const requestData = {
      ...shipment,
      updateType: coverUpdateTypeToBackendString(shipment.updateType)
    };
    const response = await axios.patch("/shipment/update", requestData);
    return response.data;
  } catch (error) {
    console.error('Error updating shipment:', error);
    throw error;
  }
};

export { getShipment, createShipment, updateShipment };