export interface CapabilityItem {
  slug: string
  title: string
  signal: string
  summary: string
  coreStack: string[]
  expandedStack: string[]
  evidence: string[]
}

export interface ArchitectureCase {
  slug: string
  title: string
  kicker: string
  summary: string
  problem: string
  constraints: string[]
  decisions: string[]
  implementation: string[]
  validation: string[]
  retrospective: string
  stack: string[]
}

export interface TechnicalNote {
  slug: string
  title: string
  kicker: string
  summary: string
  coreQuestion: string
  breakdown: string[]
  practiceChecklist: string[]
  commonPitfalls: string[]
  nextDirections: string[]
  tags: string[]
}

export const capabilityMap: CapabilityItem[] = [
  {
    slug: 'modeling',
    title: '需求建模',
    signal: '把模糊输入变成可执行结构',
    summary: '从页面、状态、接口和数据关系入手，先建立边界和词汇，再进入实现。',
    coreStack: ['领域拆解', '接口契约', '状态流转'],
    expandedStack: ['TypeScript 类型建模', 'REST 资源边界', 'OpenAPI 文档', '错误码语义'],
    evidence: ['把入口、状态、服务和文档拆成可独立验证的单元', '用测试和契约约束命名、返回结构与失败路径']
  },
  {
    slug: 'frontend',
    title: '前端体验',
    signal: '用组件和状态承载复杂交互',
    summary: '关注页面信息层级、组件边界、跨端约束和构建反馈，让界面能长期演进。',
    coreStack: ['Vue 3', 'TypeScript', 'Taro'],
    expandedStack: ['Nuxt', 'Pinia', 'Vite', 'Tailwind CSS', '响应式布局'],
    evidence: ['将页面行为沉淀到配置、状态和组合式结构里', '通过构建、类型检查和静态资源验证保护体验一致性']
  },
  {
    slug: 'backend',
    title: '后端服务',
    signal: '让接口、服务和数据规则各归其位',
    summary: '围绕服务分层、校验、鉴权、文档和可测试性设计后端结构。',
    coreStack: ['Java', 'Spring Boot', 'Spring MVC'],
    expandedStack: ['JPA', 'Validation', 'Actuator', 'SpringDoc OpenAPI', 'JWT'],
    evidence: ['把控制器、服务、仓储和 DTO 的职责拆清楚', '用集成测试覆盖接口成功路径、失败路径和边界输入']
  },
  {
    slug: 'delivery',
    title: '数据与部署',
    signal: '把环境差异收进配置和迁移策略',
    summary: '用迁移、容器和部署约束保护数据演进，让本地和发布路径可复现。',
    coreStack: ['MySQL', 'Redis', 'Docker Compose'],
    expandedStack: ['Flyway', 'Nginx', '环境变量模板', '健康检查', '静态站点发布'],
    evidence: ['用 Flyway 管理结构演进，避免手工改表漂移', '用 Compose、Nginx 和构建产物约定固定发布入口']
  },
  {
    slug: 'verification',
    title: '测试验证',
    signal: '把质量要求前置到日常节奏',
    summary: '把代码质量、单元测试、集成测试、构建检查和文档同步当成工程闭环的一部分。',
    coreStack: ['Vitest', 'JUnit', 'Jacoco'],
    expandedStack: ['vue-tsc', '内容约束测试', '静态资源检查', 'Docker 配置校验'],
    evidence: ['用测试描述内容、接口和配置必须满足的行为', '每次改动都留下可复查的构建与验证证据']
  }
]

export const architectureCases: ArchitectureCase[] = [
  {
    slug: 'module-boundary-governance',
    title: '模块边界治理',
    kicker: 'ARCHITECTURE / BOUNDARY',
    summary: '把页面、状态、接口和领域规则拆成稳定边界，降低长期维护时的连锁改动。',
    problem: '当页面逻辑、请求逻辑和领域判断混在一起时，任何一次字段调整都会牵动多个入口，修改风险会被放大。',
    constraints: ['不能为了形式化分层引入过重框架', '现有目录和命名要保持团队可读', '改造过程中页面行为必须稳定'],
    decisions: ['先固定页面、状态、服务、数据模型的依赖方向', '把请求封装和错误映射下沉到基础设施层', '让页面只消费已经整理好的视图状态'],
    implementation: ['梳理页面入口和状态来源，标记跨层调用点', '把重复的请求、转换和提示逻辑收敛为共享工具', '用文档记录模块职责，避免后续继续横向扩散'],
    validation: ['补充状态转换和接口调用的测试', '运行构建检查类型边界', '用代码审查清单复核依赖方向'],
    retrospective: '边界治理的重点不是移动文件，而是让修改路径可预期。先收敛依赖方向，再逐步抽出复用单元，风险更低。',
    stack: ['Vue 3', 'TypeScript', '状态建模', '接口封装']
  },
  {
    slug: 'api-contract-governance',
    title: '接口契约治理',
    kicker: 'API / CONTRACT',
    summary: '用统一的接口语义、文档和测试，把前后端联调从口头约定变成可验证契约。',
    problem: '接口字段、成功码、错误语义和鉴权 header 如果分散维护，前后端很容易在联调阶段才发现不一致。',
    constraints: ['浏览器端不能暴露内部服务地址', '接口文档要跟实现同步', '错误路径需要能被测试稳定复现'],
    decisions: ['管理端请求固定走同源代理，避免公开运行时配置泄漏内部地址', '服务端用 OpenAPI 与测试约束 endpoint、状态和响应结构', '文档变更作为 API 修改的必要输出'],
    implementation: ['梳理 API base、代理目标和 CORS 边界', '把 endpoint、请求封装和失败处理集中维护', '同步更新接口文档和测试指南'],
    validation: ['运行服务端接口测试', '运行管理端运行时配置验证脚本', '复核文档中的路径、端口和健康检查地址'],
    retrospective: '接口治理不是写更多文档，而是让代码、配置、测试和文档围绕同一份契约互相校验。',
    stack: ['Spring Boot', 'OpenAPI', 'Nuxt Proxy', 'TypeScript']
  },
  {
    slug: 'data-evolution-deployment',
    title: '数据演进与部署约束',
    kicker: 'DATA / DELIVERY',
    summary: '把结构迁移、环境变量、容器网络和发布入口放进明确规则，减少环境差异带来的返工。',
    problem: '数据库初始化脚本、迁移文件、容器端口和静态资源目录如果职责不清，发布时很难判断哪个位置才是事实来源。',
    constraints: ['本地开发服务不得暴露到公网', '真实密钥不能进入代码仓库', '静态站点发布必须和构建产物一致'],
    decisions: ['业务表结构以 Flyway migration 为准', '本地 Docker 端口绑定到 127.0.0.1', '个人官网以 site/dist 作为唯一静态发布物'],
    implementation: ['整理配置说明、部署说明和 Docker Compose 约束', '让 MySQL、Redis、API、管理端和 H5 调试服务各自固定职责', '为静态站点准备 Nginx 根目录和缓存策略'],
    validation: ['运行 Docker Compose 配置检查', '运行服务端迁移相关测试', '构建 site 并检查 dist 产物和 favicon'],
    retrospective: '部署稳定性来自清晰的事实来源。迁移、配置和发布物越明确，排障时越能快速定位责任边界。',
    stack: ['Flyway', 'MySQL', 'Redis', 'Docker Compose', 'Nginx']
  },
  {
    slug: 'quality-gate-practice',
    title: '质量门禁实践',
    kicker: 'QUALITY / VERIFICATION',
    summary: '把测试、类型检查、静态资源校验和文档同步变成改动前后的固定动作。',
    problem: '只靠人工检查容易漏掉内容边界、资源路径、类型漂移和配置回归，尤其是在多端项目里。',
    constraints: ['验证命令必须能在本地快速运行', '测试要覆盖真实约束而不是只检查实现细节', '文档和构建产物要跟随发布目标同步'],
    decisions: ['用 Vitest 约束个人官网内容模型和公开文案', '用 Gradle/JUnit/Jacoco 保护后端行为', '为静态资源和运行时配置编写脚本化检查'],
    implementation: ['把内容数量、slug 唯一性和禁用表达写成测试', '将构建命令作为发布前验证步骤', '在文档里记录每类改动需要同步的位置'],
    validation: ['运行 Vitest 内容测试', '运行 vue-tsc 与 Vite build', '复核 dist 目录和部署配置'],
    retrospective: '质量门禁最有价值的地方，是把“我记得要检查”变成“命令会告诉我有没有漏”。',
    stack: ['Vitest', 'vue-tsc', 'JUnit', 'Jacoco', '文档同步']
  }
]

export const technicalNotes: TechnicalNote[] = [
  {
    slug: 'source-reading-method',
    title: '源码阅读如何落到工程判断',
    kicker: 'READING',
    summary: '源码阅读不是背 API，而是提炼框架如何组织边界、调度状态和处理失败。',
    coreQuestion: '如何把框架源码里的设计取舍，带回日常项目里的结构判断？',
    breakdown: ['先追入口和生命周期，再看异常路径', '记录模块之间的依赖方向，而不是只记函数名', '把读到的模式回写到组件、状态和工具函数设计中'],
    practiceChecklist: ['画出最小调用链', '标记可替换边界', '写一段可复用的项目约定'],
    commonPitfalls: ['一开始就陷入细枝末节', '只读成功路径', '没有把阅读结论落回自己的代码'],
    nextDirections: ['Vue 响应式调度', 'Vite 插件流水线', 'Spring 请求处理链'],
    tags: ['源码', '方法', '抽象']
  },
  {
    slug: 'api-contract-notes',
    title: '接口契约的稳定表达',
    kicker: 'CONTRACT',
    summary: '接口契约要同时服务实现、联调、测试和文档，不只是路径列表。',
    coreQuestion: '怎样让接口变更在实现、文档和前端调用之间保持同步？',
    breakdown: ['统一成功、失败和鉴权语义', '把 endpoint 常量和请求封装集中维护', '让测试覆盖字段、状态和错误路径'],
    practiceChecklist: ['变更接口时同步文档', '为失败路径准备可复现输入', '避免浏览器端暴露内部地址'],
    commonPitfalls: ['只更新实现不更新文档', '把错误消息散落在页面里', '让不同端各自拼接请求地址'],
    nextDirections: ['OpenAPI 校验', '契约测试', '错误码治理'],
    tags: ['API', '文档', '测试']
  },
  {
    slug: 'state-modeling-notes',
    title: '复杂页面的状态建模',
    kicker: 'STATE',
    summary: '复杂页面先建模再编码，能减少 watch、条件渲染和请求时序互相缠绕。',
    coreQuestion: '一个页面什么时候需要把状态从组件局部提升为明确模型？',
    breakdown: ['区分原始数据、派生数据和 UI 暂态', '把请求状态、空状态和错误状态显式化', '用类型约束状态之间的合法转换'],
    practiceChecklist: ['列出状态来源', '定义页面可见状态', '为关键转换补测试或最小复现'],
    commonPitfalls: ['把接口返回直接塞进模板', '多个组件重复维护同一份状态', '失败状态只靠提示语处理'],
    nextDirections: ['Pinia store 边界', '组合式函数拆分', '表单状态机'],
    tags: ['Vue', 'TypeScript', '状态']
  },
  {
    slug: 'data-evolution-notes',
    title: '数据结构演进的安全节奏',
    kicker: 'DATA',
    summary: '数据演进需要事实来源、迁移路径和回滚意识，不能只靠初始化脚本。',
    coreQuestion: '如何让数据结构变化在开发、测试和发布环境里保持一致？',
    breakdown: ['用迁移文件描述结构变化', '把 seed 数据和结构迁移分开看待', '让服务启动过程承担迁移执行'],
    practiceChecklist: ['确认 migration 命名和顺序', '避免手工改表成为隐藏状态', '在文档中标记事实来源'],
    commonPitfalls: ['把初始化脚本当成长期 schema', '迁移和代码字段不同步', '缺少测试环境验证'],
    nextDirections: ['Flyway baseline', '数据兼容策略', '索引与查询计划'],
    tags: ['Flyway', 'MySQL', '演进']
  },
  {
    slug: 'deployment-config-notes',
    title: '部署配置的可复现性',
    kicker: 'DELIVERY',
    summary: '配置越分散，问题越难复现；发布路径要能从文档、命令和产物互相印证。',
    coreQuestion: '如何让本地调试、容器运行和静态发布的入口都清楚可查？',
    breakdown: ['把私有配置放进环境变量模板之外', '明确容器端口、服务名和健康检查', '静态站点以构建产物为准'],
    practiceChecklist: ['检查端口是否只绑定本地', '检查证书和域名配置是否可替换', '构建后确认 dist 里有入口和 favicon'],
    commonPitfalls: ['把真实密钥写进仓库', '开发端口暴露到公网', 'Nginx 根目录和实际发布目录不一致'],
    nextDirections: ['Nginx 缓存策略', 'HTTPS 切换', '发布前检查清单'],
    tags: ['Docker', 'Nginx', '配置']
  },
  {
    slug: 'testing-quality-notes',
    title: '测试如何表达工程质量',
    kicker: 'QUALITY',
    summary: '测试不只是防回归，它也能表达系统应该保持的结构和内容边界。',
    coreQuestion: '哪些约束值得写成测试，而不是留在口头约定里？',
    breakdown: ['内容边界、slug 唯一性和链接完整性适合测试', '接口成功路径和失败路径都要有样例', '构建检查能捕获类型和资源问题'],
    practiceChecklist: ['先写能失败的测试', '让测试描述行为而不是实现细节', '发布前运行完整构建'],
    commonPitfalls: ['只测快乐路径', '测试和真实入口脱节', '修改文档但没有改验证命令'],
    nextDirections: ['端到端冒烟测试', '视觉回归检查', '质量报告自动化'],
    tags: ['Vitest', 'JUnit', '验证']
  }
]

export const findArchitectureCase = (slug: string) => architectureCases.find((entry) => entry.slug === slug)

export const findTechnicalNote = (slug: string) => technicalNotes.find((entry) => entry.slug === slug)
