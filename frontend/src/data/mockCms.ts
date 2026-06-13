export type RoleCode = "ADMIN" | "EDITOR" | "REVIEWER" | "EMPLOYEE" | "VIEWER";
export type ContentType = "ARTICLE" | "DOCUMENT" | "NOTICE";
export type ContentStatus = "DRAFT" | "IN_REVIEW" | "REJECTED" | "APPROVED" | "SCHEDULED" | "PUBLISHED" | "ARCHIVED" | "EXPIRED";

export type Category = {
  id: string;
  parentId?: string | null;
  name: string;
  slug: string;
  description: string;
  sortOrder: number;
  isActive: boolean;
};

export type Attachment = {
  id: string;
  fileName: string;
  mimeType: string;
  sizeBytes: number;
  scanStatus: "PENDING" | "CLEAN" | "INFECTED" | "FAILED";
};

export type ContentSummary = {
  id: string;
  contentType: ContentType;
  status: ContentStatus;
  title: string;
  slug: string;
  summary: string;
  category: Category;
  tags: string[];
  author: string;
  updatedAt: string;
  publishedAt?: string;
  isImportant: boolean;
  requiresAcknowledgement: boolean;
  views: number;
};

export type ContentDetail = ContentSummary & {
  body: string;
  versionNumber: number;
  audiences: string[];
  attachments: Attachment[];
};

export type NoticeSummary = ContentSummary & {
  acknowledgementStatus: "PENDING" | "ACKNOWLEDGED" | "EXEMPTED";
  acknowledgedAt?: string;
};

export const categories: Category[] = [
  { id: "cat-security", name: "Security", slug: "security", description: "보안 정책과 사고 대응", sortOrder: 1, isActive: true },
  { id: "cat-engineering", name: "Engineering", slug: "engineering", description: "개발 표준과 릴리스 운영", sortOrder: 2, isActive: true },
  { id: "cat-hr", name: "HR", slug: "hr", description: "인사 제도와 복리후생", sortOrder: 3, isActive: true },
  { id: "cat-policy", name: "Policy", slug: "policy", description: "전사 운영 정책", sortOrder: 4, isActive: true }
];

const [security, engineering, hr, policy] = categories;

export const contents: ContentDetail[] = [
  {
    id: "content-password-policy",
    contentType: "ARTICLE",
    status: "PUBLISHED",
    title: "계정 보안 기준 개정 안내",
    slug: "password-policy-2026",
    summary: "다중 인증, 비밀번호 재사용 제한, 관리자 계정 점검 기준을 정리했습니다.",
    category: security,
    tags: ["security", "account", "policy"],
    author: "Security Ops",
    updatedAt: "2026-06-12T09:00:00Z",
    publishedAt: "2026-06-12T10:00:00Z",
    isImportant: true,
    requiresAcknowledgement: false,
    views: 482,
    versionNumber: 3,
    audiences: ["ALL_EMPLOYEES"],
    attachments: [{ id: "att-password", fileName: "security-checklist.pdf", mimeType: "application/pdf", sizeBytes: 864000, scanStatus: "CLEAN" }],
    body: "## 주요 변경\n\n- 관리자 계정은 하드웨어 키 기반 MFA를 기본으로 사용합니다.\n- 서비스 계정은 분기별 소유자 확인을 거칩니다.\n\n| 항목 | 기준 |\n| --- | --- |\n| MFA | 필수 |\n| 비밀번호 재사용 | 최근 12개 제한 |\n\n```bash\nsecurity-audit --scope accounts\n```"
  },
  {
    id: "content-release-runbook",
    contentType: "DOCUMENT",
    status: "PUBLISHED",
    title: "서비스 릴리스 런북",
    slug: "service-release-runbook",
    summary: "정기 배포 전후 확인 항목, 롤백 판단 기준, 장애 커뮤니케이션 흐름입니다.",
    category: engineering,
    tags: ["release", "runbook", "operations"],
    author: "Platform Team",
    updatedAt: "2026-06-11T15:30:00Z",
    publishedAt: "2026-06-11T15:45:00Z",
    isImportant: false,
    requiresAcknowledgement: false,
    views: 328,
    versionNumber: 5,
    audiences: ["ROLE:EMPLOYEE", "DEPARTMENT:Engineering"],
    attachments: [{ id: "att-release", fileName: "rollback-template.xlsx", mimeType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", sizeBytes: 428000, scanStatus: "CLEAN" }],
    body: "## 릴리스 체크포인트\n\n릴리스 오너는 배포 24시간 전 변경 범위와 롤백 플랜을 등록합니다.\n\n1. 배포 대상 서비스 확인\n2. 데이터 마이그레이션 검증\n3. 모니터링 알림 임계치 확인\n4. 고객 공지 필요 여부 확인"
  },
  {
    id: "content-benefits",
    contentType: "ARTICLE",
    status: "PUBLISHED",
    title: "2026 복리후생 신청 가이드",
    slug: "benefits-guide-2026",
    summary: "건강검진, 교육비, 원격근무 장비 신청 절차와 증빙 기준입니다.",
    category: hr,
    tags: ["benefits", "hr"],
    author: "People Team",
    updatedAt: "2026-06-10T04:20:00Z",
    publishedAt: "2026-06-10T05:00:00Z",
    isImportant: false,
    requiresAcknowledgement: false,
    views: 271,
    versionNumber: 2,
    audiences: ["ALL_EMPLOYEES"],
    attachments: [],
    body: "## 신청 기준\n\n복리후생 신청은 월 단위로 정산하며, 영수증과 결재 내역을 함께 제출합니다.\n\n### 자주 묻는 항목\n\n- 건강검진: 연 1회\n- 교육비: 팀장 승인 후 신청\n- 원격근무 장비: 입사 후 1회"
  },
  {
    id: "notice-quarterly-security",
    contentType: "NOTICE",
    status: "PUBLISHED",
    title: "분기 보안 교육 확인 요청",
    slug: "quarterly-security-training",
    summary: "6월 21일까지 보안 교육을 수강하고 확인 처리를 완료해 주세요.",
    category: security,
    tags: ["notice", "security"],
    author: "Security Ops",
    updatedAt: "2026-06-13T01:00:00Z",
    publishedAt: "2026-06-13T01:30:00Z",
    isImportant: true,
    requiresAcknowledgement: true,
    views: 614,
    versionNumber: 1,
    audiences: ["ALL_EMPLOYEES"],
    attachments: [],
    body: "## 확인 필요\n\n분기 보안 교육은 전 임직원 필수 과정입니다. 교육 완료 후 포털에서 확인 버튼을 선택해 기록을 남겨 주세요."
  },
  {
    id: "draft-api-style-guide",
    contentType: "ARTICLE",
    status: "IN_REVIEW",
    title: "API 스타일 가이드 초안",
    slug: "api-style-guide",
    summary: "사내 REST API 네이밍과 오류 응답 규칙을 표준화합니다.",
    category: engineering,
    tags: ["api", "standard"],
    author: "콘텐츠 편집자",
    updatedAt: "2026-06-13T03:20:00Z",
    isImportant: false,
    requiresAcknowledgement: false,
    views: 0,
    versionNumber: 1,
    audiences: ["DEPARTMENT:Engineering"],
    attachments: [],
    body: "## 검토 범위\n\n엔드포인트 네이밍, 페이지네이션, 오류 코드, 감사 로그 연결 규칙을 포함합니다."
  }
];

export const requiredNotices: NoticeSummary[] = [
  {
    ...contents.find((item) => item.id === "notice-quarterly-security")!,
    acknowledgementStatus: "PENDING"
  }
];

export const auditLogs = [
  { id: "audit-1", actor: "콘텐츠 편집자", action: "CONTENT_SUBMITTED", target: "API 스타일 가이드 초안", summary: "검토자에게 승인 요청", createdAt: "2026-06-13T03:21:00Z" },
  { id: "audit-2", actor: "Security Ops", action: "CONTENT_PUBLISHED", target: "분기 보안 교육 확인 요청", summary: "전 임직원 대상 공지 발행", createdAt: "2026-06-13T01:30:00Z" },
  { id: "audit-3", actor: "관리자", action: "ROLE_ASSIGNED", target: "reviewer@example.com", summary: "REVIEWER 역할 부여", createdAt: "2026-06-12T08:10:00Z" }
];

export function publishedContent() {
  return contents.filter((item) => item.status === "PUBLISHED");
}

export function searchContent(query: string, type?: ContentType) {
  const normalized = query.trim().toLowerCase();
  return publishedContent().filter((item) => {
    const matchesQuery = !normalized || [item.title, item.summary, item.body, item.category.name, item.tags.join(" ")].join(" ").toLowerCase().includes(normalized);
    const matchesType = !type || item.contentType === type;
    return matchesQuery && matchesType;
  });
}
