# SmartMES Lite Frontend

A modern Manufacturing Execution System (MES) frontend built with Vue 3, Element Plus, and ECharts.

## Features

### 1. Downtime Exception Reporting
- **Report Page**: 3-step wizard for reporting equipment downtime
  - Step 1: Select equipment
  - Step 2: Choose exception type
  - Step 3: Enter description and upload photos
- **Exception List**: View and manage all reported exceptions
  - Filter by equipment, type, and status
  - Color-coded status indicators
  - Response and resolution workflow

### 2. Production Dashboard
- **Real-time Statistics**
  - Total work orders today
  - Completed work orders
  - In-progress work orders
  - Exception work orders
  - Trend indicators

- **Visual Analytics**
  - Production completion rate (progress circle)
  - Equipment status distribution (pie chart)
  - Exception type distribution (pie chart)
  - Equipment failure TOP 5 (bar chart)

- **Auto-refresh**: Data automatically refreshes every 30 seconds

## Tech Stack

- **Vue 3** - Progressive JavaScript framework
- **Element Plus** - Vue 3 UI component library
- **Vue Router** - Official router for Vue.js
- **Axios** - Promise-based HTTP client
- **ECharts** - Powerful charting library
- **Vite** - Next generation frontend tooling

## Project Structure

```
smartmes-frontend/
├── public/              # Static assets
├── src/
│   ├── api/            # API service layer
│   │   ├── request.js  # Axios configuration
│   │   ├── downtime.js # Downtime APIs
│   │   └── dashboard.js # Dashboard APIs
│   ├── assets/         # Project assets
│   │   └── styles/     # Global styles
│   ├── components/     # Reusable components
│   │   ├── DowntimeForm.vue
│   │   ├── StatCard.vue
│   │   └── EChartsCard.vue
│   ├── router/         # Vue Router configuration
│   │   └── index.js
│   ├── views/          # Page components
│   │   ├── Dashboard/
│   │   │   └── ProductionDashboard.vue
│   │   └── Downtime/
│   │       ├── DowntimeReport.vue
│   │       └── DowntimeList.vue
│   ├── App.vue         # Root component
│   └── main.js         # Application entry
├── index.html          # HTML entry point
├── vite.config.js      # Vite configuration
└── package.json        # Project dependencies

```

## Getting Started

### Prerequisites

- Node.js >= 16.0.0
- npm >= 8.0.0

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start development server:
```bash
npm run dev
```

3. Build for production:
```bash
npm run build
```

4. Preview production build:
```bash
npm run preview
```

## API Configuration

The application expects a backend API running at `http://localhost:3000`. You can modify the API base URL in `vite.config.js`:

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-url',
      changeOrigin: true
    }
  }
}
```

## API Endpoints

### Downtime APIs
- `GET /api/equipment/list` - Get equipment list
- `POST /api/downtime/report` - Submit downtime report
- `GET /api/downtime/list` - Get downtime list
- `PUT /api/downtime/:id/respond` - Respond to downtime
- `PUT /api/downtime/:id/resolve` - Resolve downtime
- `POST /api/upload/image` - Upload image

### Dashboard APIs
- `GET /api/dashboard/order-stats` - Get order statistics
- `GET /api/dashboard/production-rate` - Get production completion rate
- `GET /api/dashboard/equipment-status` - Get equipment status distribution
- `GET /api/dashboard/downtime-distribution` - Get downtime type distribution
- `GET /api/dashboard/equipment-failure-top5` - Get top 5 equipment failures

## Features Overview

### Downtime Reporting
The downtime reporting feature allows operators to quickly report equipment issues with a simple 3-step process:
1. Select the affected equipment from a dropdown
2. Choose the exception type (equipment failure, material shortage, quality issue, human error, or other)
3. Provide a detailed description and optionally upload photos

### Exception Management
The exception list provides a comprehensive view of all reported issues with:
- Real-time status updates (Pending, In Progress, Resolved)
- Color-coded status indicators for quick identification
- Filtering capabilities by equipment, type, and status
- Action buttons for responding to and resolving exceptions
- Detailed view of each exception with complete history

### Production Dashboard
The dashboard provides real-time visibility into production operations:
- Key metrics displayed in attractive stat cards with trend indicators
- Visual progress tracking for daily production targets
- Interactive charts showing equipment status and failure patterns
- Automatic data refresh every 30 seconds
- Manual refresh option for immediate updates

## Customization

### Styling
The application uses Element Plus theme variables and custom CSS. You can customize colors and styles in:
- `src/App.vue` - Global styles
- Individual component `<style>` sections
- Element Plus theme variables

### Charts
ECharts configurations can be customized in:
- `src/views/Dashboard/ProductionDashboard.vue`
- Modify the `*Option` objects to change chart appearance and behavior

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

Copyright © 2024 SmartMES Lite. All rights reserved.
