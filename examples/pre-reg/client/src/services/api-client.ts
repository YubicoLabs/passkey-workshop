import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';
import {
  Product,
  Address,
  ValidateAddressRequest,
  ValidateAddressResponse,
  Country,
  CreateShipmentRequest,
  Shipment,
  ShipmentListResponse,
  ProductSchema,
  ValidateAddressResponseSchema,
  CountrySchema,
  ShipmentSchema,
  ShipmentListResponseSchema,
} from '@/types/api';
import type { CountriesResponse } from '../components/order-flow/AddressForm';

export interface ApiClientConfig {
  baseURL: string;
  apiKey?: string;
  getIdToken?: () => Promise<string>;
}

export class YubiKeyApiClient {
  private client: AxiosInstance;
  private config: ApiClientConfig;

  constructor(config: ApiClientConfig) {
    this.config = config;
    this.client = axios.create({
      baseURL: config.baseURL,
      timeout: 30000, 
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor for authentication
    this.client.interceptors.request.use(async (reqConfig) => {
      if (this.config.apiKey) {
        reqConfig.headers['X-API-Key'] = this.config.apiKey;
      }

      if (this.config.getIdToken) {
        try {
          const token = await this.config.getIdToken();
          reqConfig.headers['Authorization'] = `Bearer ${token}`;
        } catch (error) {
          console.error('Failed to get ID token:', error);
        }
      }

      return reqConfig;
    });

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response) {
          // Server responded with error status
          const { status, data } = error.response;
          
          switch (status) {
            case 401:
              console.error('Unauthorized: Invalid credentials');
              break;
            case 403:
              console.error('Forbidden: Access denied');
              break;
            case 404:
              console.error('Not found');
              break;
            case 422:
              console.error('Validation error:', data);
              break;
            case 500:
              console.error('Server error');
              break;
          }
        } else if (error.request) {
          console.error('Network error: No response received');
        } else if (axios.isCancel(error)) {
          console.error('Request canceled:', error.message);
        } else {
          console.error('Error', error.message);
        }
        
        return Promise.reject(error);
      }
    );
  }

  // Products endpoints
  async getProducts(): Promise<Product[]> {
    const response = await this.client.get('/products');
    // Handle array response
    if (Array.isArray(response.data)) {
      return response.data.map((item: any) => ProductSchema.parse(item));
    }
    return [];
  }

  async getProduct(productId: string): Promise<Product> {
    const response = await this.client.get(`/products/${productId}`);
    return ProductSchema.parse(response.data);
  }

  // Address endpoints
  async validateAddress(request: ValidateAddressRequest): Promise<ValidateAddressResponse> {
    const response = await this.client.post('/addresses/validate', request);
    const deliverable = response.data && response.data.status === 'Deliverable address' && (!response.data.errors || response.data.errors.length === 0);
    return {
      validated: deliverable,
      errors: response.data.errors ? response.data.errors.map((e: any) => e.message || JSON.stringify(e)) : [],
      suggestedAddress: response.data.address,
    };
  }

  // Countries endpoint
  async getCountries(): Promise<CountriesResponse> {
    const response = await this.client.get('/countries');
    if (response.data && Array.isArray(response.data.countries)) {
      return {
        count: response.data.count,
        total_count: response.data.total_count,
        countries: response.data.countries.map((item: any) => ({
          country_id: item.country_id,
          country_name: item.country_name,
          country_code_2: item.country_code_2,
          country_code_3: item.country_code_3,
          country_vat_rate: item.country_vat_rate,
          delivery_types: item.delivery_types,
          states: item.states ?? [],
        })),
      };
    }
    return { count: 0, total_count: 0, countries: [] };
  }

  async getCountry(countryCode: string): Promise<Country> {
    const response = await this.client.get(`/countries/${countryCode}`);
    return CountrySchema.parse(response.data);
  }

  // Shipment endpoints
  async createShipment(request: CreateShipmentRequest): Promise<Shipment> {
    const response = await this.client.post('/fido2PreRegisteredShipments', request);
    
    // The response data should already be a plain object
    // Don't try to parse the request, parse the response
    try {
      return ShipmentSchema.parse(response.data);
    } catch (error) {
      console.error('Failed to parse shipment response:', error);
      console.error('Response data:', response.data);
      // Return the raw data if parsing fails (for debugging)
      return response.data as Shipment;
    }
  }

  async getShipment(shipmentId: string): Promise<Shipment> {
    const response = await this.client.get(`/fido2PreRegisteredShipments/${shipmentId}`);
    try {
      return ShipmentSchema.parse(response.data);
    } catch (error) {
      console.error('Failed to parse shipment:', error);
      return response.data as Shipment;
    }
  }

  async getShipments(params?: {
    page?: number;
    pageSize?: number;
    status?: string;
  }): Promise<ShipmentListResponse> {
    const response = await this.client.get('/fido2PreRegisteredShipments', { params });
    try {
      return ShipmentListResponseSchema.parse(response.data);
    } catch (error) {
      console.error('Failed to parse shipments list:', error);
      return response.data as ShipmentListResponse;
    }
  }

  async cancelShipment(shipmentId: string): Promise<void> {
    await this.client.delete(`/fido2PreRegisteredShipments/${shipmentId}`);
  }
}

// Factory function for creating API client
export const createApiClient = (config: Partial<ApiClientConfig> = {}): YubiKeyApiClient => {
  const defaultConfig: ApiClientConfig = {
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
    ...config,
  };
  
  return new YubiKeyApiClient(defaultConfig);
};