# 📱 ROADMAP: Aplicación Móvil FarmaControl con Ionic Angular

## 📊 Análisis de la API Backend

### ✅ **Módulos API Disponibles:**
- **👤 Usuarios y Autenticación** - 8 endpoints (Login, CRUD, roles)
- **🔐 Roles** - 6 endpoints (20 roles: ADMIN, FARMACEUTICO, CAJERO, etc.)
- **📦 Productos** - 6 endpoints (CRUD + filtros + stock)
- **🏷️ Categorías** - 6 endpoints (20 categorías disponibles)
- **👥 Clientes** - 6 endpoints (Gestión completa)
- **🏢 Proveedores** - 6 endpoints (Gestión completa)
- **🛒 Compras** - 6 endpoints (Con detalles y filtros)
- **💰 Ventas** - 6 endpoints (Con detalles y filtros)
- **📊 Reportes** - 12 endpoints (Dashboard + estadísticas)

### 🎯 **Base URL:** `http://localhost:8080/api`

---

## 🚀 Aplicación Móvil: FarmaControl Mobile

### 📋 **Concepto General**
Aplicación móvil **sencilla y ágil** para gestión farmacéutica con roles diferenciados, enfocada en:
- ✅ **Punto de Venta (POS)** móvil para cajeros
- ✅ **Inventario** para almacenistas y farmacéuticos
- ✅ **Dashboard** de administración
- ✅ **Reportes** móviles en tiempo real

---

## 🏗️ ARQUITECTURA TÉCNICA

### **Stack Tecnológico:**
```bash
📱 Framework: Ionic 7 + Angular 17
🎨 UI: Ionic Components + Material Design
📡 HTTP: Angular HttpClient + RxJS
🔐 Auth: JWT/Session + Role Guards
📱 Plataforma: iOS + Android (Capacitor)
🌐 API: REST (Java Spring Boot)
```

### **Estructura del Proyecto:**
```
src/app/
├── core/                   # Servicios base y configuración
│   ├── services/
│   │   ├── api.service.ts
│   │   ├── auth.service.ts
│   │   └── storage.service.ts
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   └── role.guard.ts
│   ├── interceptors/
│   │   └── auth.interceptor.ts
│   └── models/             # Interfaces TypeScript
│       ├── user.model.ts
│       ├── product.model.ts
│       └── sale.model.ts
├── shared/                 # Componentes compartidos
│   ├── components/
│   │   ├── header/
│   │   ├── loading/
│   │   └── product-card/
│   └── pipes/
├── modules/               # Módulos principales
│   ├── auth/
│   ├── dashboard/
│   ├── pos/               # Punto de Venta
│   ├── inventory/
│   └── reports/
└── tabs/                  # Navegación principal
```

---

## 📱 DISEÑO DE LA APLICACIÓN

### 🎨 **Layout Principal: Tab Navigation**

#### **Tabs por Rol:**

**👑 ADMIN:**
```
┌─────────────────────────────┐
│ [🏠 Dashboard] [📊 Reports] │
│ [📦 Inventory] [👥 Users]   │
└─────────────────────────────┘
```

**💊 FARMACEUTICO:**
```
┌─────────────────────────────┐
│ [🏠 Dashboard] [📦 Stock]   │
│ [🛒 Purchases] [📋 Orders]  │
└─────────────────────────────┘
```

**💰 CAJERO:**
```
┌─────────────────────────────┐
│ [🛍️ POS] [📋 Sales] [👤 Me]│
└─────────────────────────────┘
```

---

## 🗂️ MÓDULOS Y COMPONENTES DETALLADOS

### 1. 🔐 **MÓDULO: Autenticación (auth/)**

#### **Páginas:**
- **`login.page.ts`** - Pantalla de login principal
- **`role-selection.page.ts`** - Selección de rol (si múltiples roles)

#### **Componentes:**
```typescript
// login.page.ts
interface LoginPage {
  // Formulario simple y elegante
  email: string;
  password: string;
  rememberMe: boolean;
  
  // Botones rápidos para demo
  quickLogin(role: 'admin' | 'cajero' | 'farmaceutico'): void;
  login(): void;
  showForgotPassword(): void;
}
```

#### **Diseño UI:**
```html
<!-- login.page.html -->
<ion-content class="login-bg">
  <div class="login-container">
    <!-- Logo animado -->
    <div class="logo-section">
      <ion-icon name="medical-outline" class="logo-icon"></ion-icon>
      <h1>FarmaControl</h1>
      <p>Sistema Móvil de Gestión</p>
    </div>
    
    <!-- Formulario -->
    <ion-card class="login-card">
      <ion-item>
        <ion-icon name="mail" slot="start"></ion-icon>
        <ion-input placeholder="Email" [(ngModel)]="email"></ion-input>
      </ion-item>
      
      <ion-item>
        <ion-icon name="lock-closed" slot="start"></ion-icon>
        <ion-input type="password" placeholder="Contraseña" [(ngModel)]="password"></ion-input>
      </ion-item>
      
      <ion-button expand="block" (click)="login()">
        <ion-icon name="log-in" slot="start"></ion-icon>
        Iniciar Sesión
      </ion-button>
    </ion-card>
    
    <!-- Demo rápido -->
    <div class="demo-section">
      <h3>🧪 Demo Rápido:</h3>
      <ion-button fill="outline" size="small" (click)="quickLogin('admin')">
        👑 Admin
      </ion-button>
      <ion-button fill="outline" size="small" (click)="quickLogin('cajero')">
        💰 Cajero
      </ion-button>
    </div>
  </div>
</ion-content>
```

#### **API Integración:**
```typescript
// auth.service.ts
@Injectable()
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private currentUser = new BehaviorSubject<User | null>(null);
  
  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/usuarios/auth`, {
      email, password
    }).pipe(
      tap(response => {
        if (response.success) {
          localStorage.setItem('user', JSON.stringify(response.data));
          this.currentUser.next(response.data);
        }
      })
    );
  }
  
  hasRole(roleId: number): boolean {
    const user = this.getCurrentUser();
    return user ? user.rolId === roleId : false;
  }
  
  isAdmin(): boolean { return this.hasRole(1); }
  isCajero(): boolean { return this.hasRole(3); }
  isFarmaceutico(): boolean { return this.hasRole(2); }
}
```

---

### 2. 🏠 **MÓDULO: Dashboard (dashboard/)**

#### **Páginas:**
- **`dashboard.page.ts`** - Vista principal con KPIs
- **`dashboard-admin.page.ts`** - Dashboard específico admin
- **`dashboard-cajero.page.ts`** - Dashboard específico cajero

#### **Componentes:**
- **`kpi-card.component.ts`** - Tarjeta de estadística
- **`quick-actions.component.ts`** - Acciones rápidas
- **`recent-activity.component.ts`** - Actividad reciente

#### **Diseño Dashboard Admin:**
```html
<!-- dashboard.page.html -->
<ion-header>
  <ion-toolbar color="primary">
    <ion-title>Dashboard</ion-title>
    <ion-buttons slot="end">
      <ion-button (click)="refresh()">
        <ion-icon name="refresh"></ion-icon>
      </ion-button>
    </ion-buttons>
  </ion-toolbar>
</ion-header>

<ion-content>
  <!-- KPIs Grid -->
  <div class="kpi-grid">
    <app-kpi-card
      title="Ventas Hoy"
      value="{{ stats.totalVentas }}"
      icon="trending-up"
      color="success">
    </app-kpi-card>
    
    <app-kpi-card
      title="Stock Bajo"
      value="{{ stats.productosStockBajo }}"
      icon="warning"
      color="warning">
    </app-kpi-card>
    
    <app-kpi-card
      title="Productos"
      value="{{ stats.totalProductos }}"
      icon="cube"
      color="primary">
    </app-kpi-card>
    
    <app-kpi-card
      title="Clientes"
      value="{{ stats.totalClientes }}"
      icon="people"
      color="tertiary">
    </app-kpi-card>
  </div>
  
  <!-- Acciones Rápidas -->
  <app-quick-actions [userRole]="currentUser.rolId"></app-quick-actions>
  
  <!-- Actividad Reciente -->
  <app-recent-activity></app-recent-activity>
</ion-content>
```

#### **API Integration:**
```typescript
// dashboard.service.ts
@Injectable()
export class DashboardService {
  
  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/reportes`);
  }
  
  getRecentSales(): Observable<Sale[]> {
    return this.http.get<Sale[]>(`${this.apiUrl}/ventas?limit=5`);
  }
  
  getProductsLowStock(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/reportes/inventario/bajo`);
  }
}
```

---

### 3. 🛍️ **MÓDULO: Punto de Venta - POS (pos/)**

#### **Páginas:**
- **`pos.page.ts`** - Interfaz principal de venta
- **`product-search.page.ts`** - Búsqueda de productos
- **`cart.page.ts`** - Carrito de compras
- **`checkout.page.ts`** - Finalizar venta
- **`receipt.page.ts`** - Recibo digital

#### **Componentes:**
- **`product-scanner.component.ts`** - Scanner de códigos
- **`product-list.component.ts`** - Lista de productos
- **`cart-summary.component.ts`** - Resumen del carrito
- **`payment-methods.component.ts`** - Métodos de pago

#### **Diseño POS:**
```html
<!-- pos.page.html -->
<ion-header>
  <ion-toolbar color="success">
    <ion-title>Punto de Venta</ion-title>
    <ion-buttons slot="end">
      <ion-badge color="primary">{{ cartCount }}</ion-badge>
      <ion-button (click)="openCart()">
        <ion-icon name="cart"></ion-icon>
      </ion-button>
    </ion-buttons>
  </ion-toolbar>
</ion-header>

<ion-content>
  <!-- Búsqueda rápida -->
  <div class="search-section">
    <ion-searchbar 
      placeholder="Buscar productos..."
      (ionInput)="searchProducts($event)"
      [debounce]="300">
    </ion-searchbar>
    
    <ion-button fill="outline" (click)="openScanner()">
      <ion-icon name="scan" slot="start"></ion-icon>
      Scanner
    </ion-button>
  </div>
  
  <!-- Categorías rápidas -->
  <ion-segment [(ngModel)]="selectedCategory" (ionChange)="filterByCategory()">
    <ion-segment-button value="all">
      <ion-label>Todos</ion-label>
    </ion-segment-button>
    <ion-segment-button value="1">
      <ion-label>Analgésicos</ion-label>
    </ion-segment-button>
    <ion-segment-button value="2">
      <ion-label>Antibióticos</ion-label>
    </ion-segment-button>
  </ion-segment>
  
  <!-- Lista de productos -->
  <div class="products-grid">
    <ion-card 
      *ngFor="let product of filteredProducts" 
      class="product-card"
      (click)="addToCart(product)">
      
      <div class="product-header">
        <h3>{{ product.nombre }}</h3>
        <ion-badge [color]="getStockColor(product.stock)">
          Stock: {{ product.stock }}
        </ion-badge>
      </div>
      
      <p>{{ product.descripcion }}</p>
      
      <div class="product-footer">
        <span class="price">${{ product.precio }}</span>
        <ion-button size="small" fill="clear">
          <ion-icon name="add"></ion-icon>
        </ion-button>
      </div>
    </ion-card>
  </div>
</ion-content>

<!-- FAB para carrito -->
<ion-fab vertical="bottom" horizontal="end">
  <ion-fab-button color="success" (click)="openCart()">
    <ion-icon name="cart"></ion-icon>
    <ion-badge>{{ cartCount }}</ion-badge>
  </ion-fab-button>
</ion-fab>
```

#### **Lógica POS:**
```typescript
// pos.page.ts
export class PosPage implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  cart: CartItem[] = [];
  selectedCategory: string = 'all';
  
  constructor(
    private posService: PosService,
    private cartService: CartService,
    private modalCtrl: ModalController
  ) {}
  
  ngOnInit() {
    this.loadProducts();
    this.cartService.cart$.subscribe(cart => {
      this.cart = cart;
    });
  }
  
  loadProducts() {
    this.posService.getProducts().subscribe(products => {
      this.products = products;
      this.filteredProducts = products;
    });
  }
  
  searchProducts(event: any) {
    const query = event.target.value.toLowerCase();
    this.filteredProducts = this.products.filter(product =>
      product.nombre.toLowerCase().includes(query) ||
      product.descripcion.toLowerCase().includes(query)
    );
  }
  
  addToCart(product: Product) {
    this.cartService.addItem(product);
    this.showToast(`${product.nombre} agregado al carrito`);
  }
  
  async openCart() {
    const modal = await this.modalCtrl.create({
      component: CartPage,
      cssClass: 'cart-modal'
    });
    await modal.present();
  }
  
  async openScanner() {
    // Integración con plugin de scanner
    const modal = await this.modalCtrl.create({
      component: ProductScannerComponent
    });
    await modal.present();
  }
}
```

---

### 4. 📦 **MÓDULO: Inventario (inventory/)**

#### **Páginas:**
- **`inventory.page.ts`** - Lista principal de productos
- **`product-detail.page.ts`** - Detalle de producto
- **`add-product.page.ts`** - Agregar nuevo producto
- **`categories.page.ts`** - Gestión de categorías
- **`stock-alerts.page.ts`** - Alertas de stock bajo

#### **Componentes:**
- **`product-card.component.ts`** - Tarjeta de producto
- **`stock-indicator.component.ts`** - Indicador visual de stock
- **`category-filter.component.ts`** - Filtro por categorías

#### **Diseño Inventario:**
```html
<!-- inventory.page.html -->
<ion-header>
  <ion-toolbar color="primary">
    <ion-title>Inventario</ion-title>
    <ion-buttons slot="end">
      <ion-button (click)="showFilters()">
        <ion-icon name="filter"></ion-icon>
      </ion-button>
      <ion-button (click)="addProduct()">
        <ion-icon name="add"></ion-icon>
      </ion-button>
    </ion-buttons>
  </ion-toolbar>
</ion-header>

<ion-content>
  <!-- Estadísticas rápidas -->
  <div class="stats-bar">
    <div class="stat-item">
      <ion-icon name="cube" color="primary"></ion-icon>
      <div>
        <span class="number">{{ totalProducts }}</span>
        <span class="label">Productos</span>
      </div>
    </div>
    <div class="stat-item">
      <ion-icon name="warning" color="warning"></ion-icon>
      <div>
        <span class="number">{{ lowStockCount }}</span>
        <span class="label">Stock Bajo</span>
      </div>
    </div>
  </div>
  
  <!-- Lista de productos con virtual scroll -->
  <ion-virtual-scroll [items]="products" approxItemHeight="120px">
    <div *virtualItem="let product; let itemBounds = bounds;">
      <ion-card class="product-card">
        <ion-card-content>
          <div class="product-info">
            <div class="product-main">
              <h3>{{ product.nombre }}</h3>
              <p>{{ product.descripcion }}</p>
              <ion-badge [color]="getCategoryColor(product.categoria.nombre)">
                {{ product.categoria.nombre }}
              </ion-badge>
            </div>
            
            <div class="product-actions">
              <div class="stock-info">
                <app-stock-indicator [current]="product.stock" [minimum]="product.stockMinimo">
                </app-stock-indicator>
                <span class="price">${{ product.precio }}</span>
              </div>
              
              <div class="action-buttons">
                <ion-button size="small" fill="clear" (click)="editStock(product)">
                  <ion-icon name="create"></ion-icon>
                </ion-button>
                <ion-button size="small" fill="clear" (click)="viewDetails(product)">
                  <ion-icon name="eye"></ion-icon>
                </ion-button>
              </div>
            </div>
          </div>
        </ion-card-content>
      </ion-card>
    </div>
  </ion-virtual-scroll>
</ion-content>

<!-- FAB para acciones -->
<ion-fab vertical="bottom" horizontal="end">
  <ion-fab-button>
    <ion-icon name="add"></ion-icon>
  </ion-fab-button>
  <ion-fab-list side="top">
    <ion-fab-button (click)="addProduct()">
      <ion-icon name="medical"></ion-icon>
    </ion-fab-button>
    <ion-fab-button (click)="addCategory()">
      <ion-icon name="folder"></ion-icon>
    </ion-fab-button>
    <ion-fab-button (click)="scanProduct()">
      <ion-icon name="scan"></ion-icon>
    </ion-fab-button>
  </ion-fab-list>
</ion-fab>
```

---

### 5. 📊 **MÓDULO: Reportes (reports/)**

#### **Páginas:**
- **`reports.page.ts`** - Dashboard de reportes
- **`sales-report.page.ts`** - Reporte de ventas
- **`inventory-report.page.ts`** - Reporte de inventario
- **`top-products.page.ts`** - Productos más vendidos

#### **Componentes:**
- **`chart.component.ts`** - Gráficos usando Chart.js
- **`report-card.component.ts`** - Tarjeta de reporte
- **`date-range-picker.component.ts`** - Selector de fechas

#### **Diseño Reportes:**
```html
<!-- reports.page.html -->
<ion-header>
  <ion-toolbar color="tertiary">
    <ion-title>Reportes</ion-title>
    <ion-buttons slot="end">
      <ion-button (click)="exportReports()">
        <ion-icon name="download"></ion-icon>
      </ion-button>
    </ion-buttons>
  </ion-toolbar>
</ion-header>

<ion-content>
  <!-- Filtros de fecha -->
  <div class="filters-section">
    <app-date-range-picker 
      (dateRangeChange)="onDateRangeChange($event)">
    </app-date-range-picker>
  </div>
  
  <!-- Reportes principales -->
  <div class="reports-grid">
    <app-report-card
      title="Ventas del Período"
      [data]="salesReport"
      type="chart"
      chartType="line">
    </app-report-card>
    
    <app-report-card
      title="Productos Más Vendidos"
      [data]="topProducts"
      type="list">
    </app-report-card>
    
    <app-report-card
      title="Stock por Categoría"
      [data]="stockByCategory"
      type="chart"
      chartType="doughnut">
    </app-report-card>
  </div>
  
  <!-- Acciones rápidas -->
  <div class="quick-reports">
    <h3>Reportes Rápidos</h3>
    <ion-list>
      <ion-item button (click)="viewReport('sales')">
        <ion-icon name="trending-up" slot="start" color="success"></ion-icon>
        <ion-label>
          <h3>Ventas del Mes</h3>
          <p>Resumen de ventas mensuales</p>
        </ion-label>
        <ion-badge slot="end">${{ monthlySales }}</ion-badge>
      </ion-item>
      
      <ion-item button (click)="viewReport('inventory')">
        <ion-icon name="cube" slot="start" color="primary"></ion-icon>
        <ion-label>
          <h3>Estado del Inventario</h3>
          <p>Stock actual y alertas</p>
        </ion-label>
        <ion-badge slot="end" color="warning">{{ lowStockItems }}</ion-badge>
      </ion-item>
    </ion-list>
  </div>
</ion-content>
```

---

## 🔧 SERVICIOS PRINCIPALES

### **ApiService (Servicio Base):**
```typescript
// core/services/api.service.ts
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly API_URL = 'http://localhost:8080/api';
  
  constructor(private http: HttpClient) {}
  
  // Métodos genéricos
  get<T>(endpoint: string, params?: any): Observable<T> {
    return this.http.get<T>(`${this.API_URL}${endpoint}`, { params });
  }
  
  post<T>(endpoint: string, data: any): Observable<T> {
    return this.http.post<T>(`${this.API_URL}${endpoint}`, data);
  }
  
  put<T>(endpoint: string, data: any): Observable<T> {
    return this.http.put<T>(`${this.API_URL}${endpoint}`, data);
  }
  
  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.API_URL}${endpoint}`);
  }
  
  // Métodos específicos para cada módulo
  
  // USUARIOS
  login(email: string, password: string) {
    return this.post('/usuarios/auth', { email, password });
  }
  
  getUsers() {
    return this.get('/usuarios');
  }
  
  // PRODUCTOS
  getProducts(categoryId?: number) {
    const params = categoryId ? { categoria_id: categoryId } : {};
    return this.get('/productos', params);
  }
  
  createProduct(product: any) {
    return this.post('/productos', product);
  }
  
  updateStock(productId: number, newStock: number) {
    return this.put(`/productos/${productId}/stock`, { nuevoStock: newStock });
  }
  
  // VENTAS
  getSales(clientId?: number, dateFrom?: string, dateTo?: string) {
    const params = { cliente_id: clientId, fecha_inicio: dateFrom, fecha_fin: dateTo };
    return this.get('/ventas', params);
  }
  
  createSale(sale: any) {
    return this.post('/ventas', sale);
  }
  
  // REPORTES
  getDashboard() {
    return this.get('/reportes');
  }
  
  getSalesReport(dateFrom?: string, dateTo?: string) {
    const params = { fecha_inicio: dateFrom, fecha_fin: dateTo };
    return this.get('/reportes/ventas', params);
  }
  
  getTopProducts() {
    return this.get('/reportes/productos/mas-vendidos');
  }
  
  getLowStockProducts() {
    return this.get('/reportes/inventario/bajo');
  }
}
```

### **CartService (Gestión del Carrito):**
```typescript
// core/services/cart.service.ts
@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartSubject = new BehaviorSubject<CartItem[]>([]);
  public cart$ = this.cartSubject.asObservable();
  
  private items: CartItem[] = [];
  
  addItem(product: Product, quantity: number = 1) {
    const existingItem = this.items.find(item => item.product.id === product.id);
    
    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      this.items.push({
        product,
        quantity,
        subtotal: product.precio * quantity
      });
    }
    
    this.updateCart();
  }
  
  removeItem(productId: number) {
    this.items = this.items.filter(item => item.product.id !== productId);
    this.updateCart();
  }
  
  updateQuantity(productId: number, quantity: number) {
    const item = this.items.find(item => item.product.id === productId);
    if (item) {
      item.quantity = quantity;
      item.subtotal = item.product.precio * quantity;
      this.updateCart();
    }
  }
  
  getTotal(): number {
    return this.items.reduce((total, item) => total + item.subtotal, 0);
  }
  
  getItemCount(): number {
    return this.items.reduce((count, item) => count + item.quantity, 0);
  }
  
  clearCart() {
    this.items = [];
    this.updateCart();
  }
  
  private updateCart() {
    this.cartSubject.next([...this.items]);
  }
}
```

---

## 📱 INTERFACES Y MODELOS

### **Modelos TypeScript:**
```typescript
// core/models/user.model.ts
export interface User {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  rolId: number;
  activo: boolean;
  role: Role;
}

export interface Role {
  id: number;
  nombre: string;
  descripcion: string;
}

// core/models/product.model.ts
export interface Product {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  stockMinimo: number;
  categoriaId: number;
  codigoBarras?: string;
  activo: boolean;
  categoria: Category;
}

export interface Category {
  id: number;
  nombre: string;
  descripcion: string;
  activo: boolean;
}

// core/models/sale.model.ts
export interface Sale {
  id?: number;
  clienteId: number;
  usuarioId: number;
  fechaVenta: string;
  total: number;
  estado: 'COMPLETADA' | 'CANCELADA' | 'PENDIENTE';
  cliente?: Client;
  detalles: SaleDetail[];
}

export interface SaleDetail {
  id?: number;
  productoId: number;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  producto?: Product;
}

export interface CartItem {
  product: Product;
  quantity: number;
  subtotal: number;
}
```

---

## 🎨 TEMAS Y ESTILOS

### **Tema Principal:**
```scss
// theme/variables.scss
:root {
  // Colores principales
  --ion-color-primary: #3880ff;
  --ion-color-secondary: #0cd1e8;
  --ion-color-tertiary: #7044ff;
  --ion-color-success: #10dc60;
  --ion-color-warning: #ffce00;
  --ion-color-danger: #f04141;
  
  // Colores farmacéuticos
  --farma-green: #28a745;
  --farma-blue: #007bff;
  --farma-red: #dc3545;
}

// Estilos globales
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  padding: 16px;
}

.product-card {
  margin: 8px;
  border-radius: 12px;
  
  .product-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .product-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
  }
  
  .price {
    font-size: 1.2em;
    font-weight: bold;
    color: var(--farma-green);
  }
}

.stats-bar {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  background: var(--ion-color-light);
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .number {
      font-size: 1.5em;
      font-weight: bold;
    }
    
    .label {
      font-size: 0.9em;
      color: var(--ion-color-medium);
    }
  }
}
```

---

## 🚀 ROADMAP DE DESARROLLO

### **Fase 1: Setup Inicial (1-2 días)**
```bash
# Crear proyecto Ionic
ionic start farmacontrol-mobile tabs --type=angular
cd farmacontrol-mobile

# Instalar dependencias
npm install @angular/common @angular/forms
npm install chart.js ng2-charts
npm install @capacitor/camera @capacitor/storage
```

### **Fase 2: Autenticación (2-3 días)**
- ✅ Crear servicio de autenticación
- ✅ Implementar guards de ruta
- ✅ Diseñar página de login
- ✅ Integrar con API de usuarios
- ✅ Manejo de roles y permisos

### **Fase 3: Dashboard (2-3 días)**
- ✅ Crear componentes de KPI
- ✅ Integrar reportes de la API
- ✅ Diseñar layouts por rol
- ✅ Implementar acciones rápidas

### **Fase 4: POS - Punto de Venta (4-5 días)**
- ✅ Crear interface de productos
- ✅ Implementar carrito de compras
- ✅ Diseñar proceso de checkout
- ✅ Integrar con API de ventas
- ✅ Generar recibos digitales

### **Fase 5: Inventario (3-4 días)**
- ✅ Lista de productos con filtros
- ✅ Gestión de categorías
- ✅ Alertas de stock bajo
- ✅ Formularios de productos

### **Fase 6: Reportes (2-3 días)**
- ✅ Dashboard de reportes
- ✅ Gráficos con Chart.js
- ✅ Exportación de datos
- ✅ Filtros por fecha

### **Fase 7: Optimizaciones (2-3 días)**
- ✅ Performance y lazy loading
- ✅ Offline capabilities
- ✅ Push notifications
- ✅ Testing final

---

## 📦 DEPENDENCIAS PRINCIPALES

### **package.json:**
```json
{
  "dependencies": {
    "@angular/core": "^17.0.0",
    "@angular/common": "^17.0.0",
    "@angular/forms": "^17.0.0",
    "@ionic/angular": "^7.5.0",
    "@capacitor/core": "^5.0.0",
    "@capacitor/camera": "^5.0.0",
    "@capacitor/storage": "^1.2.5",
    "chart.js": "^4.4.0",
    "ng2-charts": "^5.0.3",
    "rxjs": "^7.8.0"
  },
  "devDependencies": {
    "@angular/cli": "^17.0.0",
    "@ionic/cli": "^7.1.0",
    "@capacitor/cli": "^5.0.0"
  }
}
```

---

## 🎯 CARACTERÍSTICAS PRINCIPALES

### ✅ **Funcionalidades Core:**
1. **🔐 Login por roles** - Admin, Cajero, Farmacéutico
2. **📊 Dashboard dinámico** - KPIs en tiempo real
3. **🛍️ POS móvil** - Venta rápida con carrito
4. **📦 Gestión inventario** - Stock y categorías
5. **📈 Reportes visuales** - Gráficos y estadísticas
6. **🔍 Búsqueda inteligente** - Productos por nombre/código
7. **📱 Responsive design** - Adaptable a tablets
8. **⚡ Offline mode** - Funciona sin internet

### ✅ **Ventajas Técnicas:**
- **Performance**: Virtual scrolling para listas grandes
- **UX**: Animaciones fluidas de Ionic
- **Security**: JWT + Role-based guards
- **Scalable**: Arquitectura modular
- **Maintainable**: TypeScript + Angular patterns

---

## 📋 CHECKLIST DE IMPLEMENTACIÓN

### **Setup Proyecto:**
- [ ] Crear proyecto Ionic con Angular
- [ ] Configurar estructura de carpetas
- [ ] Instalar dependencias necesarias
- [ ] Configurar variables de entorno

### **Desarrollo Core:**
- [ ] Implementar servicio de API
- [ ] Crear sistema de autenticación
- [ ] Diseñar guards de rutas
- [ ] Implementar modelos TypeScript

### **Módulos Principales:**
- [ ] Dashboard con KPIs
- [ ] POS con carrito de compras
- [ ] Inventario con filtros
- [ ] Reportes con gráficos

### **Testing y Optimización:**
- [ ] Testing de servicios
- [ ] Testing de componentes
- [ ] Optimización de performance
- [ ] Build para producción

---

**📱 TIEMPO ESTIMADO TOTAL: 16-20 días de desarrollo**

**🎯 RESULTADO: Aplicación móvil profesional, sencilla y ágil para gestión farmacéutica con roles diferenciados y funcionalidades específicas para cada tipo de usuario.**

---

*Este roadmap está diseñado para crear una aplicación móvil funcional y profesional que aproveche al máximo los 80+ endpoints de tu API FarmaControl.*