import { http, HttpResponse, delay } from 'msw';
import { 
  Product, 
  Country, 
  Shipment, 
  ValidateAddressResponse,
  ShipmentListResponse 
} from '@/features/orders/types';

// Mock data
const mockProducts: Product[] = [
  {
    id: 'yubikey-5c-nfc-usba',
    name: 'YubiKey 5C NFC',
    description: 'USB-C with NFC for mobile',
    price: 55,
    currency: 'USD',
    image: '/images/yubikey-5c-nfc.png',
    formFactor: 'USB-C',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP', 'NFC'],
    inStock: true,
  },
  {
    id: 'yubikey-5-nfc-usba',
    name: 'YubiKey 5 NFC',
    description: 'USB-A with NFC for mobile',
    price: 50,
    currency: 'USD',
    image: '/images/yubikey-5-nfc.png',
    formFactor: 'USB-A',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP', 'NFC'],
    inStock: true,
  },
  {
    id: 'yubikey-5c-nano',
    name: 'YubiKey 5C Nano',
    description: 'Ultra-small USB-C form factor',
    price: 60,
    currency: 'USD',
    image: '/images/yubikey-5c-nano.png',
    formFactor: 'Nano',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP'],
    inStock: true,
  },
  {
    id: 'yubikey-5-nano',
    name: 'YubiKey 5 Nano',
    description: 'Ultra-small USB-A form factor',
    price: 50,
    currency: 'USD',
    image: '/images/yubikey-5-nano.png',
    formFactor: 'Nano',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP'],
    inStock: true,
  },
];

const mockCountries: Country[] = [
  {
    code: 'US',
    name: 'United States',
    states: [
      { code: 'NY', name: 'New York' },
      { code: 'CA', name: 'California' },
      { code: 'TX', name: 'Texas' },
      { code: 'FL', name: 'Florida' },
      { code: 'WA', name: 'Washington' },
    ],
  },
  {
    code: 'CA',
    name: 'Canada',
    states: [
      { code: 'ON', name: 'Ontario' },
      { code: 'QC', name: 'Quebec' },
      { code: 'BC', name: 'British Columbia' },
      { code: 'AB', name: 'Alberta' },
    ],
  },
  {
    code: 'GB',
    name: 'United Kingdom',
  },
  {
    code: 'DE',
    name: 'Germany',
  },
  {
    code: 'FR',
    name: 'France',
  },
];

const mockShipments: Shipment[] = [];

// Helper to generate mock shipment
const generateShipmentId = () => `SHIP-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
const generateOrderId = () => `#EXAMPLE${Math.floor(Math.random() * 10000)}`;

export const handlers = [
  // Products endpoints
  http.get('/api/products', async () => {
    await delay(300);
    return HttpResponse.json(mockProducts);
  }),

  http.get('/api/products/:productId', async ({ params }) => {
    await delay(200);
    const product = mockProducts.find(p => p.id === params.productId);
    
    if (!product) {
      return new HttpResponse(null, { status: 404 });
    }
    
    return HttpResponse.json(product);
  }),

  // Address validation endpoint
  http.post('/api/addresses/validate', async ({ request }) => {
    await delay(500);
    const body = await request.json() as any;
    
    const response: ValidateAddressResponse = {
      validated: true,
      suggestedAddress: body.address,
      errors: [],
    };

    // Simulate validation errors for specific cases
    if (body.address?.postalCode === '00000') {
      response.validated = false;
      response.errors = ['Invalid postal code'];
    }

    return HttpResponse.json(response);
  }),

  // Countries endpoints
  http.get('/api/countries', async () => {
    await delay(200);
    return HttpResponse.json(mockCountries);
  }),

  http.get('/api/countries/:countryCode', async ({ params }) => {
    await delay(150);
    const country = mockCountries.find(c => c.code === params.countryCode);
    
    if (!country) {
      return new HttpResponse(null, { status: 404 });
    }
    
    return HttpResponse.json(country);
  }),

  // Shipment endpoints
  http.post('/api/fido2PreRegisteredShipments', async ({ request }) => {
    await delay(800);
    const body = await request.json() as any;
    
    const newShipment: Shipment = {
      id: generateShipmentId(),
      orderId: generateOrderId(),
      status: 'PROCESSING',
      products: body.products,
      shippingAddress: body.shippingAddress,
      userEmail: body.userEmail,
      requestDate: new Date().toISOString(),
      requestor: body.userEmail,
      metadata: body.metadata,
    };
    
    mockShipments.push(newShipment);
    
    return HttpResponse.json(newShipment, { status: 202 });
  }),

  http.get('/api/fido2PreRegisteredShipments/:shipmentId', async ({ params }) => {
    await delay(300);
    let shipment = mockShipments.find(s => s.id === params.shipmentId);
    
    if (!shipment) {
      // Generate a mock shipment for demo purposes
      shipment = {
        id: params.shipmentId as string,
        orderId: generateOrderId(),
        status: Math.random() > 0.5 ? 'SHIPPED' : 'PROCESSING',
        products: [
          {
            product: mockProducts[0],
            quantity: 1,
            isPrimary: true,
          },
          {
            product: mockProducts[1],
            quantity: 1,
            isPrimary: false,
          },
        ],
        shippingAddress: {
          firstName: 'John',
          lastName: 'Doe',
          addressLine1: '1111 Street Street',
          city: 'New York',
          stateProvince: 'NY',
          postalCode: '10001',
          country: 'USA',
          phone: '212-000-0000',
        },
        userEmail: 'user@example.com',
        requestDate: new Date(Date.now() - 86400000).toISOString(),
        requestor: 'user@example.com',
        trackingNumber: shipment?.status === 'SHIPPED' ? '9400111234567890123456' : undefined,
        carrier: shipment?.status === 'SHIPPED' ? 'USPS' : undefined,
      };
    }
    
    return HttpResponse.json(shipment);
  }),

  http.get('/api/fido2PreRegisteredShipments', async ({ request }) => {
    await delay(400);
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '1');
    const pageSize = parseInt(url.searchParams.get('pageSize') || '10');
    const status = url.searchParams.get('status');
    
    let filteredShipments = [...mockShipments];
    
    if (status) {
      filteredShipments = filteredShipments.filter(s => s.status === status);
    }

    // Add some default shipments if none exist
    if (filteredShipments.length === 0) {
      filteredShipments = [
        {
          id: 'SHIP-DEMO-001',
          orderId: '#EXAMPLE1234',
          status: 'DELIVERED',
          products: [
            { product: mockProducts[0], quantity: 1, isPrimary: true },
            { product: mockProducts[1], quantity: 1, isPrimary: false },
          ],
          shippingAddress: {
            firstName: 'User',
            lastName: 'Name',
            addressLine1: '1111 Street Street',
            city: 'New York',
            stateProvince: 'NY',
            postalCode: '10001',
            country: 'USA',
            phone: '212-000-0000',
          },
          userEmail: 'username@example.com',
          requestDate: '2025-05-26T08:13:00Z',
          requestor: 'username@example.com',
          trackingNumber: '9400111234567890123456',
          carrier: 'USPS',
          actualDelivery: '2025-05-29T15:30:00Z',
        },
        {
          id: 'SHIP-DEMO-002',
          orderId: '#EXAMPLE5678',
          status: 'SHIPPED',
          products: [
            { product: mockProducts[2], quantity: 2, isPrimary: true },
          ],
          shippingAddress: {
            firstName: 'User',
            lastName: 'Name',
            addressLine1: '1111 Street Street',
            city: 'New York',
            stateProvince: 'NY',
            postalCode: '10001',
            country: 'USA',
            phone: '212-000-0000',
          },
          userEmail: 'username@example.com',
          requestDate: '2025-06-15T10:30:00Z',
          requestor: 'username@example.com',
          trackingNumber: '9400111234567890123457',
          carrier: 'USPS',
        },
      ];
    }
    
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const paginatedShipments = filteredShipments.slice(start, end);
    
    const response: ShipmentListResponse = {
      shipments: paginatedShipments,
      total: filteredShipments.length,
      page,
      pageSize,
    };
    
    return HttpResponse.json(response);
  }),

  http.delete('/api/fido2PreRegisteredShipments/:shipmentId', async ({ params }) => {
    await delay(300);
    const index = mockShipments.findIndex(s => s.id === params.shipmentId);
    
    if (index === -1) {
      return new HttpResponse(null, { status: 404 });
    }
    
    mockShipments.splice(index, 1);
    return new HttpResponse(null, { status: 204 });
  }),
];