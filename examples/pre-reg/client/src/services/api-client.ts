import axios, { AxiosInstance } from 'axios';
import {
  ValidateAddressRequest,
  ValidateAddressResponse,
  ValidateAddressResponseSchema,
  ShipmentSchema,
  Shipment,
  CountriesResponse,
  CountriesResponseSchema,
  ApiValidateAddressRequest,
  ApiValidateAddressRequestSchema,
  ApiValidateAddressResponseSchema,
  ShipmentRequest
} from '@/types/api';

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

  async validateAddress(request: ValidateAddressRequest): Promise<ValidateAddressResponse> {
    let region = request.address.stateProvince;
    if (request.address.country === 'US' && request.address.stateProvince) {
      const usStates: Record<string, string> = {
        'Alabama': 'AL', 'Alaska': 'AK', 'Arizona': 'AZ', 'Arkansas': 'AR', 'California': 'CA', 'Colorado': 'CO',
        'Connecticut': 'CT', 'Delaware': 'DE', 'Florida': 'FL', 'Georgia': 'GA', 'Hawaii': 'HI', 'Idaho': 'ID',
        'Illinois': 'IL', 'Indiana': 'IN', 'Iowa': 'IA', 'Kansas': 'KS', 'Kentucky': 'KY', 'Louisiana': 'LA',
        'Maine': 'ME', 'Maryland': 'MD', 'Massachusetts': 'MA', 'Michigan': 'MI', 'Minnesota': 'MN', 'Mississippi': 'MS',
        'Missouri': 'MO', 'Montana': 'MT', 'Nebraska': 'NE', 'Nevada': 'NV', 'New Hampshire': 'NH', 'New Jersey': 'NJ',
        'New Mexico': 'NM', 'New York': 'NY', 'North Carolina': 'NC', 'North Dakota': 'ND', 'Ohio': 'OH', 'Oklahoma': 'OK',
        'Oregon': 'OR', 'Pennsylvania': 'PA', 'Rhode Island': 'RI', 'South Carolina': 'SC', 'South Dakota': 'SD',
        'Tennessee': 'TN', 'Texas': 'TX', 'Utah': 'UT', 'Vermont': 'VT', 'Virginia': 'VA', 'Washington': 'WA',
        'West Virginia': 'WV', 'Wisconsin': 'WI', 'Wyoming': 'WY'
      };
      region = usStates[region] || region;
    }
    const apiRequest: ApiValidateAddressRequest = {
      street_line1: request.address.addressLine1,
      street_line2: request.address.addressLine2 || undefined,
      city: request.address.city,
      postal_code: request.address.postalCode,
      region,
      country_code_2: request.address.country
    };
    const validatedApiRequest = ApiValidateAddressRequestSchema.parse(apiRequest);
    const response = await this.client.post('/addresses/validate', validatedApiRequest);
    const apiResponse = ApiValidateAddressResponseSchema.parse(response.data);
    
    // Map API response back to your internal format
    const mappedResponse: ValidateAddressResponse = {
      validated: apiResponse.status === 'deliverable',
      errors: apiResponse.details ?
        apiResponse.details.map((detail: any) =>
          typeof detail === 'string' ? detail : JSON.stringify(detail)
        ) : [],
      suggestedAddress: apiResponse.address ? {
        // Map API format back to Address format
        firstName: request.address.firstName, // Preserve from original
        lastName: request.address.lastName,   // Preserve from original
        addressLine1: apiResponse.address.street_line1,
        addressLine2: apiResponse.address.street_line2 || '',
        city: apiResponse.address.city,
        stateProvince: apiResponse.address.region || '',
        postalCode: apiResponse.address.postal_code,
        country: apiResponse.address.country_code_2,
        phone: request.address.phone // Preserve from original
      } : undefined
    };

    // Validate the final mapped response
    return ValidateAddressResponseSchema.parse(mappedResponse);
  }

  // Countries endpoint
  async getCountries(): Promise<CountriesResponse> {
    const response = await this.client.get('/countries');
    const validatedData = CountriesResponseSchema.parse(response.data);
    return validatedData;
  }

  // Shipment endpoints
  async createShipment(request: ShipmentRequest): Promise<Shipment> {
    const response = await this.client.post('/shipments', request);
    // Extract shipment_id from response.data.data
    const shipmentData = response.data?.data;
    try {
      // Validate response shape at runtime
      return ShipmentSchema.parse(shipmentData);
    } catch (error) {
      console.error('Failed to parse shipment response:', error);
      console.error('Response data:', shipmentData);
      // Return raw data for debugging if validation fails
      return shipmentData as Shipment;
    }
  }
}

// Factory function for creating API client
export const createApiClient = (config: Partial<ApiClientConfig> = {}): YubiKeyApiClient => {
  const defaultConfig: ApiClientConfig = {
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8086/api',
    ...config,
  };

  return new YubiKeyApiClient(defaultConfig);
};