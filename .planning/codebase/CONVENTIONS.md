# Coding Conventions

**Analysis Date:** 2026-04-26

## Backend (Java/Spring Boot)

### Project Structure

```
backend/src/main/java/com/stallmart/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── service/         # Business logic interfaces
│   └── impl/        # Service implementations
├── repository/      # Data access (MyBatis-Plus mappers)
├── model/
│   ├── entity/      # Database entities
│   ├── dto/         # Data transfer objects
│   └── vo/          # View objects (API responses)
├── common/
│   ├── exception/   # Exception handling
│   ├── filter/      # Filters (e.g., JWT auth)
│   ├── result/      # Unified response wrapper
│   └── security/    # Security utilities
```

### Naming Conventions

**Classes:**
- PascalCase: `AuthController`, `UserServiceImpl`, `WechatLoginDTO`
- Entity classes: `User`, `Product`, `Order` (noun, singular)
- DTO suffix: `WechatLoginDTO`, `BindPhoneDTO`, `CreateOrderDTO`
- VO suffix: `AuthVO`, `UserVO`, `ProductVO`
- Mapper suffix: `UserMapper`, `ProductMapper`

**Methods:**
- camelCase: `wechatLogin()`, `findByOpenid()`, `createOrUpdate()`
- Use descriptive verbs: `createOrUpdate`, `findById`, `updatePhone`

**Variables:**
- camelCase: `openid`, `userId`, `accessToken`
- Chinese comments for documentation

### Code Style

**Annotations:** Spring Boot conventions
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
```

**Lombok Usage:**
- `@Data` - Entity classes, DTOs, VOs
- `@Slf4j` - Service implementations
- `@RequiredArgsConstructor` - Service, Controller classes
- `@TableName`, `@TableId`, `@TableField` - MyBatis-Plus entities

**API Documentation:**
- Swagger annotations: `@Tag`, `@Operation`
```java
@Tag(name = "认证", description = "微信登录、Token刷新、登出")
@RestController
@RequestMapping("/api/v1/auth")
```

**Validation:**
```java
@NotBlank(message = "code 不能为空")
private String code;
```

### Import Organization

Standard order:
1. `java.*` packages
2. `org.springframework.*` packages
3. `com.baomidou.*` (MyBatis-Plus)
4. `lombok` packages
5. Internal `com.stallmart.*` packages

---

## Mini-Program (TypeScript/Taro/Vue3)

### Project Structure

```
mini-program/src/
├── config/           # App configuration
├── pages/            # Taro pages
│   ├── customer/     # Customer-facing pages
│   └── vendor/       # Vendor-facing pages
├── store/            # Pinia stores
├── utils/            # Utility functions
├── app.ts            # App entry
└── app.scss          # Global styles
```

### Naming Conventions

**Files:**
- camelCase or kebab-case: `user.ts`, `request.ts`, `my-orders.config.ts`
- Page config files: `[page-name].config.ts`
- Store files: `user.ts`, `store/index.ts`

**TypeScript:**
- Interfaces: PascalCase with optional `I` prefix avoided, use `UserInfo`, `UserState`
- Type definitions inline with function signatures
- Strict TypeScript with `strict: true`

### Code Style

**TypeScript Configuration** (`tsconfig.json`):
- `strict: true`
- `noUnusedLocals: true`
- `noUnusedParameters: true`
- `noImplicitReturns: true`
- `noFallthroughCasesInSwitch: true`
- Path alias: `@/*` maps to `src/*`

**Vue3 Composition API:**
```typescript
export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const isLoading = ref(false)

  const isLoggedIn = computed(() => !!userInfo.value)

  function setUserInfo(info: UserInfo | null) {
    userInfo.value = info
  }

  return { userInfo, isLoading, isLoggedIn, setUserInfo }
})
```

**Logging Pattern:**
```typescript
console.error('[Global Error]', err)
console.log('[App] onLaunch', options)
```

### Linting & Formatting

**ESLint** (configured via `package.json`):
- `@typescript-eslint/eslint-plugin`
- `@typescript-eslint/parser`
- `eslint-plugin-vue`
- `eslint-plugin-prettier`

**Commands:**
```bash
npm run lint              # Run ESLint
npm run lint:fix          # Fix ESLint issues
npm run format            # Format with Prettier
```

**Prettier Configuration:**
- Default settings (no `.prettierrc` found, uses defaults)
- Formats: `.vue`, `.js`, `.jsx`, `.ts`, `.tsx`, `.json`, `.css`, `.scss`

### Styling

**Tailwind CSS:**
- Custom colors defined in `tailwind.config.js`
- Brand colors: `primary` (#FF6B35), `secondary` (#2EC4B6)
- Custom shadows: `card`, `elevated`
- Border radius: `xl` (12px), `2xl` (16px)
- `corePlugins.preflight: false` (Taro compatibility)
- `important: true` for Tailwind specificity

**Global Styles:**
- File: `src/app.scss`
- Custom font family: PingFang SC, Helvetica Neue, Arial

### API Layer

**Request Utility** (`src/utils/request.ts`):
- Axios-like wrapper around `Taro.request`
- Bearer token authentication
- Automatic token refresh on 401
- Response interceptor pattern

**API Functions:**
```typescript
export function get<T>(url: string, data?: any, options?: Partial<RequestOptions>)
export function post<T>(url: string, data?: any, options?: Partial<RequestOptions>)
export function put<T>(url: string, data?: any, options?: Partial<RequestOptions>)
export function del<T>(url: string, data?: any, options?: Partial<RequestOptions>)
```

---

## Cross-Project Conventions

### API Response Format

**Backend returns:**
```json
{
  "code": 200,
  "message": "success",
  "data": {...},
  "timestamp": 1743443200
}
```

**Mini-program expects:**
```typescript
interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}
```

### Error Handling

**Backend:**
- `BusinessException` for business logic errors
- `GlobalExceptionHandler` for centralized handling
- Error codes by module (1xxxx: general, 2xxxx: user, etc.)

**Mini-program:**
- Error handling in request interceptor
- HTTP status code mapping (401: expired, 403: no permission, 404: not found)

### Authentication

- JWT tokens: `accessToken` (2 hour expiry), `refreshToken`
- Bearer token in Authorization header
- Redis blacklist for logout

---

*Convention analysis: 2026-04-26*
