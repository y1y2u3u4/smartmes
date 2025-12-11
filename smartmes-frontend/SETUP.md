# SmartMES Lite Frontend - Setup Guide

## Quick Start

### 1. Install Dependencies

```bash
cd smartmes-frontend
npm install
```

Or using pnpm (faster):

```bash
pnpm install
```

### 2. Start Development Server

```bash
npm run dev
```

The application will open automatically at `http://localhost:5173`

## First Time Setup

### Prerequisites Check

Make sure you have the following installed:

```bash
# Check Node.js version (should be >= 16)
node --version

# Check npm version (should be >= 8)
npm --version
```

### Environment Configuration

The project includes a default `.env` file. For local development, you can create `.env.local`:

```env
# API Base URL
VITE_API_BASE_URL=/api
```

### Backend Connection

The frontend is configured to proxy API requests to the backend:

- **Frontend URL**: `http://localhost:5173`
- **Backend URL**: `http://localhost:3000` (configured in vite.config.js)
- **API Proxy**: All `/api/*` requests are forwarded to the backend

Make sure the backend server is running before starting the frontend.

## Available Scripts

### Development

```bash
npm run dev
```

Starts the Vite development server with hot-reload.

### Build

```bash
npm run build
```

Builds the application for production to the `dist/` folder.

### Preview

```bash
npm run preview
```

Preview the production build locally.

## Project Features Implemented

### Work Order Management

1. **Work Order List** (`/workorders`)
   - ✅ Paginated table display
   - ✅ Search and filter functionality
   - ✅ Status color coding
   - ✅ CRUD operations

2. **Create/Edit Work Order** (`/workorders/create`)
   - ✅ Form validation
   - ✅ Product/Equipment/Operator selection
   - ✅ Auto-generated work order numbers
   - ✅ Date picker for scheduling

3. **Work Order Detail** (`/workorders/:id`)
   - ✅ Comprehensive information display
   - ✅ Progress tracking
   - ✅ Action buttons (Start/Complete)

### UI Components

- ✅ App Layout (Header, Sidebar, Main)
- ✅ Collapsible sidebar menu
- ✅ Breadcrumb navigation
- ✅ Status tags with colors
- ✅ Data table with pagination

### API Integration

- ✅ Axios configuration with interceptors
- ✅ Error handling
- ✅ API path configuration
- ✅ Work Order API endpoints

## Troubleshooting

### Port Already in Use

If port 5173 is already in use:

```bash
# Kill the process using the port (macOS/Linux)
lsof -ti:5173 | xargs kill -9

# Or change the port in vite.config.js
server: {
  port: 3000  // Change to your preferred port
}
```

### Dependencies Installation Fails

Try clearing the cache:

```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

### API Connection Issues

1. Check if the backend is running:
   ```bash
   curl http://localhost:3000/api/health
   ```

2. Check proxy configuration in `vite.config.js`

3. Check browser console for CORS errors

### Hot Reload Not Working

1. Save the file again
2. Restart the dev server
3. Clear browser cache (Cmd+Shift+R or Ctrl+Shift+R)

## Next Steps

### Recommended Development Flow

1. **Start Backend** (in separate terminal):
   ```bash
   cd smartmes-backend
   npm start
   ```

2. **Start Frontend**:
   ```bash
   cd smartmes-frontend
   npm run dev
   ```

3. **Access Application**:
   - Open browser to `http://localhost:5173`
   - Navigate to Work Orders page
   - Test CRUD operations

### Testing the Application

1. **Work Order List**:
   - View the list of work orders
   - Use search filters
   - Test pagination

2. **Create Work Order**:
   - Click "Create Work Order" button
   - Fill in the form
   - Submit and verify

3. **Work Order Detail**:
   - Click on any work order
   - View details
   - Test Start/Complete actions

## Code Style

The project follows Vue 3 Composition API best practices:

- Use `<script setup>` syntax
- Prefer `ref` and `reactive` for state
- Use `computed` for derived state
- Organize imports at the top
- Add JSDoc comments for complex functions

## Additional Resources

- [Vue 3 Documentation](https://vuejs.org/)
- [Element Plus Documentation](https://element-plus.org/)
- [Vite Documentation](https://vitejs.dev/)
- [Vue Router Documentation](https://router.vuejs.org/)

## Support

For issues or questions:
1. Check the console for errors
2. Review the API response in Network tab
3. Verify backend is running
4. Contact the development team
