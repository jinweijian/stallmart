# Testing Patterns

**Analysis Date:** 2026-04-26

## Backend Testing (Java/Spring Boot)

### Test Framework

**Framework:** JUnit 5 (via `spring-boot-starter-test`)
- Jupiter test engine
- Spring Boot Test annotations
- Mockito for mocking

**Build Tool:** Maven
- Test execution: `mvn test`
- Spring Boot plugin: `mvn spring-boot:run`

### Test File Organization

**Location:** `backend/src/test/java/`

**Naming:** `*Test.java` suffix
- Example: `UserServiceTest.java`

**Package Structure:** Mirrors main source
```
backend/src/test/java/com/stallmart/
└── service/
    └── UserServiceTest.java
```

### Test Structure

**Spring Boot Test Class:**
```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateOrUpdate_NewUser() {
        String openid = "test_openid_" + System.currentTimeMillis();
        User user = userService.createOrUpdate(openid, "unionid", "测试用户", "https://example.com/avatar.png");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(openid, user.getOpenid());
        assertFalse(user.getHasPhone());
        assertEquals("customer", user.getRole());
    }
}
```

**Assertion Library:** JUnit 5 assertions (`org.junit.jupiter.api.Assertions`)

**Patterns:**
- `@SpringBootTest` for integration tests
- `@Autowired` for dependency injection in tests
- `System.currentTimeMillis()` for unique test data
- Direct entity assertions with `assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`

### Test Coverage

**Current Status:** Minimal
- Only one test file found: `UserServiceTest.java`
- Tests cover basic CRUD operations

**Missing:**
- Controller tests
- Service unit tests (without Spring context)
- Integration tests with `@DataJpaTest` or `@WebMvcTest`

### Backend Test Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Mini-Program Testing (TypeScript/Taro/Vue3)

### Test Framework

**Status:** Not configured
- No Jest, Vitest, or other test framework found
- No test files (`*.test.*`, `*.spec.*`) in `src/` directory

### Test Configuration Files

**Jest/Vitest:** Not present
- No `jest.config.*` files
- No `vitest.config.*` files

**Babel Configuration** (`babel.config.js`):
```javascript
module.exports = {
  presets: [
    ['@babel/preset-env', { targets: { node: 'current' } }],
    ['@babel/preset-typescript', { isTSX: true, allExtensions: true }],
  ],
}
```

### Test Dependencies

**Package.json devDependencies include:**
- `@babel/core`, `@babel/preset-env`, `@babel/preset-typescript`
- TypeScript tooling

**Not installed:**
- Jest or Vitest
- Testing libraries (`@testing-library/*`, `vue-test-utils`)
- Assertion libraries

### Manual Testing Approach

Current development uses:
- Taro build verification: `npm run dev:weapp`
- H5 preview: `npm run dev:h5`
- Manual WeChat DevTools testing

---

## Recommendations

### Backend Testing Improvements

1. **Add Controller Tests:**
   ```java
   @WebMvcTest(AuthController.class)
   class AuthControllerTest {
       @Autowired
       private MockMvc mockMvc;

       @Test
       void testWechatLogin() throws Exception {
           mockMvc.perform(post("/api/v1/auth/wechat/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"code\":\"test_code\"}"))
               .andExpect(status().isOk());
       }
   }
   ```

2. **Add Service Unit Tests:**
   ```java
   @ExtendWith(MockitoExtension.class)
   class AuthServiceTest {
       @Mock
       private UserService userService;

       @InjectMocks
       private AuthServiceImpl authService;

       @Test
       void testWechatLogin_MockWechatApi() {
           // Mock external WeChat API calls
       }
   }
   ```

3. **Add Test Coverage Enforcement:**
   ```xml
   <plugin>
       <groupId>org.jacoco</groupId>
       <artifactId>jacoco-maven-plugin</artifactId>
   </plugin>
   ```

### Mini-Program Testing Improvements

1. **Install Test Framework:**
   ```bash
   npm install -D vitest @vue/test-utils
   ```

2. **Configure Vitest** (`vitest.config.ts`):
   ```typescript
   import { defineConfig } from 'vitest/config'
   import vue from '@vitejs/plugin-vue'

   export default defineConfig({
     plugins: [vue()],
     test: {
       environment: 'jsdom',
       include: ['src/**/*.{test,spec}.{js,ts}']
     }
   })
   ```

3. **Add Test Scripts:**
   ```json
   {
     "test": "vitest",
     "test:coverage": "vitest --coverage"
   }
   ```

4. **Example Store Test:**
   ```typescript
   import { describe, it, expect } from 'vitest'
   import { useUserStore } from '@/store/user'

   describe('useUserStore', () => {
     it('should set user info', () => {
       const store = useUserStore()
       store.setUserInfo({ id: '1', openid: 'test', nickname: 'Test', avatar: '', phone: '', role: 'customer', createdAt: '' })
       expect(store.isLoggedIn).toBe(true)
     })
   })
   ```

---

*Testing analysis: 2026-04-26*
