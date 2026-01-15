export enum InventoryStatus {
  AVAILABLE = 'AVAILABLE',
  NOT_AVAILABLE = 'NOT_AVAILABLE',
}

export interface ProductInventoryResponse {
  id: string;
  productMasterId: string;
  productName: string;
  supermarketId: string;
  supermarketName: string;
  originalPrice: number;
  sellingPrice: number;
  quantityAvailable: number;
  expiryDate: number; // timestamp in milliseconds
  status: InventoryStatus;
  createdById?: string;
  createdByName?: string;
  createdAt: number; // timestamp in milliseconds
  updatedAt: number; // timestamp in milliseconds
}

export interface CreateProductInventoryRequest {
  productMasterId: string;
  supermarketId: string;
  originalPrice: number;
  sellingPrice: number;
  quantityAvailable: number;
  expiryDate: number; // timestamp in milliseconds
}

export interface UpdateProductInventoryRequest {
  originalPrice?: number;
  sellingPrice?: number;
  quantityAvailable?: number;
  expiryDate?: number; // timestamp in milliseconds
  status?: InventoryStatus;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}
