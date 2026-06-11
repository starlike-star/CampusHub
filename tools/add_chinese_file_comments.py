from __future__ import annotations

from pathlib import Path
import re


ROOT = Path(__file__).resolve().parents[1]


EXPLICIT_DESCRIPTIONS = {
    "pom.xml": "定义 Maven 项目的依赖、Java 版本、测试与打包配置。",
    "src/main/resources/database.properties": "配置数据库驱动、连接地址、用户名和密码等运行参数。",
    "src/main/webapp/WEB-INF/web.xml": "配置 Web 应用的欢迎页、会话与容器级部署参数。",
    "src/main/resources/database/schema.sql": "定义 CampusHub 数据库的基础表结构、约束、索引与初始化数据。",
    "database/migrations/20260610_add_goods_trade_method.sql": "为商品交易方式功能补充数据库字段及相关数据迁移。",
    "src/main/webapp/index.jsp": "渲染应用主框架，提供侧边导航、全局弹窗和前端资源入口。",
    "src/main/webapp/js/activity-actions.js": "处理活动发布、编辑、报名、取消报名、状态变更和删除等前端交互。",
    "src/main/webapp/js/app-router.js": "实现基于 URL Hash 的页面路由、异步内容加载和主界面初始化。",
    "src/main/webapp/js/image-upload.js": "封装图片选择、上传、预览、数量限制与失败提示交互。",
    "src/main/webapp/js/index.js": "初始化首页通用交互、导航状态和全局组件行为。",
    "src/main/webapp/js/lostfound-actions.js": "处理失物招领信息的发布、编辑、认领、状态变更和删除交互。",
    "src/main/webapp/js/market.js": "处理二手市场筛选、商品发布编辑、收藏和状态操作。",
    "src/main/webapp/js/message-actions.js": "处理站内通知的已读、全部已读和未读数量刷新。",
    "src/main/webapp/js/post-detail.js": "处理帖子详情页的评论、点赞、收藏、编辑与删除交互。",
    "src/main/webapp/js/private-messages.js": "处理私信会话列表、消息发送、轮询刷新与滚动定位。",
    "src/main/webapp/js/profile-actions.js": "处理个人资料编辑、头像上传、签到及个人内容操作。",
    "src/main/webapp/js/register.js": "处理注册表单校验、验证码刷新和密码可见性切换。",
    "src/main/webapp/js/report.js": "提供内容举报弹窗、原因校验和举报请求提交。",
    "src/main/webapp/js/textarea-autosize.js": "根据输入内容自动调整多行文本框高度。",
    "src/main/webapp/js/trade.js": "处理商品交易下单、模拟支付、二维码展示和订单状态轮询。",
}


ACTION_WORDS = {
    "Create": "创建",
    "Update": "更新",
    "Delete": "删除",
    "Detail": "详情查询",
    "Status": "状态变更",
    "Favorite": "收藏切换",
    "Like": "点赞切换",
    "Register": "报名",
    "CancelRegister": "取消报名",
    "ReadAll": "全部标记已读",
    "Read": "标记已读",
    "UnreadCount": "未读数量查询",
    "Send": "发送",
    "Thread": "会话详情",
    "Handle": "审核处理",
    "Confirm": "确认",
    "Page": "页面展示",
}


DOMAIN_NAMES = {
    "Account": "账号",
    "ActivityRegistration": "活动报名",
    "Activity": "活动",
    "Admin": "后台管理",
    "ClaimRequest": "认领申请",
    "Comment": "评论",
    "Experience": "经验值",
    "GoodsOrder": "商品订单",
    "Goods": "商品",
    "Home": "首页",
    "LostFound": "失物招领",
    "Message": "站内通知",
    "Notice": "公告",
    "Post": "帖子",
    "PrivateConversation": "私信会话",
    "PrivateMessage": "私信消息",
    "Profile": "个人主页",
    "PublicUserProfile": "公开用户主页",
    "RememberToken": "记住登录令牌",
    "Report": "举报",
    "Search": "全站搜索",
    "Square": "校园广场",
    "TradeOrder": "交易订单",
    "User": "用户",
}


def split_java_name(stem: str, suffix: str) -> str:
    return stem[: -len(suffix)] if stem.endswith(suffix) else stem


def domain_label(name: str) -> str:
    for key in sorted(DOMAIN_NAMES, key=len, reverse=True):
        if name.startswith(key):
            return DOMAIN_NAMES[key]
    return name


def java_description(path: Path) -> str:
    rel = path.relative_to(ROOT).as_posix()
    stem = path.stem
    if "/test/" in f"/{rel}/":
        target = stem.removesuffix("Test")
        return f"验证 {domain_label(target)}相关逻辑的正常路径、边界条件和失败场景。"
    if "/config/" in rel:
        return "集中读取并校验数据库连接配置，为 JDBC 访问提供统一配置来源。"
    if "/constant/" in rel:
        return "集中定义会话属性名等跨模块共享常量，避免散落的字符串字面量。"
    if "/dao/" in rel:
        if stem.startswith("Jdbc"):
            target = split_java_name(stem.removeprefix("Jdbc"), "Dao")
            return f"使用 JDBC 实现{domain_label(target)}数据的查询与持久化操作。"
        target = split_java_name(stem, "Dao")
        return f"定义{domain_label(target)}数据访问能力及业务层依赖的数据契约。"
    if "/filter/" in rel:
        labels = {
            "AdminAuthFilter": "拦截后台请求并校验当前用户是否具有管理员权限。",
            "AuthFilter": "拦截受保护请求，确保用户登录后才能继续访问。",
            "EncodingFilter": "统一请求与响应字符编码，避免中文参数和页面内容乱码。",
            "RememberMeFilter": "在会话缺少登录用户时尝试通过持久令牌恢复登录状态。",
        }
        return labels.get(stem, "在请求进入 Servlet 前执行统一的访问控制或预处理。")
    if "/model/" in rel:
        if stem.endswith("Result"):
            return f"封装{domain_label(stem.removesuffix('Result'))}操作的处理结果与返回数据。"
        if stem.endswith("VO"):
            return f"聚合{domain_label(stem.removesuffix('VO'))}页面展示所需的数据。"
        if stem.endswith("Info"):
            return f"承载{domain_label(stem.removesuffix('Info'))}相关的只读信息。"
        if stem.endswith("Log"):
            return f"记录{domain_label(stem.removesuffix('Log'))}变更明细及审计信息。"
        if stem.endswith("Target"):
            return f"描述{domain_label(stem.removesuffix('Target'))}消息或跳转的目标信息。"
        return f"表示系统中的{domain_label(stem)}领域数据，并提供对应属性访问。"
    if "/service/" in rel:
        if stem == "ServiceResult":
            return "统一封装业务操作的成功状态、提示消息和可选返回数据。"
        target = split_java_name(stem, "Service")
        return f"编排{domain_label(target)}业务规则、参数校验与数据访问操作。"
    if "/servlet/" in rel:
        if stem.endswith("JsonSupport"):
            target = split_java_name(stem, "JsonSupport")
            return f"为{domain_label(target)}接口提供统一的 JSON 响应和参数处理辅助能力。"
        if stem == "ContentServlet":
            return "根据前端路由参数分发并渲染各业务页面片段。"
        if stem == "CaptchaServlet":
            return "生成图形验证码并将校验值保存到当前会话。"
        if stem == "ImageUploadServlet":
            return "校验并保存用户上传的图片，返回可访问的图片地址。"
        if stem == "LoginServlet":
            return "处理登录页面展示、凭据校验、会话建立与记住登录选项。"
        if stem == "LogoutServlet":
            return "清理登录会话与持久登录令牌并完成退出跳转。"
        if stem == "RegisterServlet":
            return "处理注册页面展示、验证码校验和新用户创建。"
        if stem == "HomeServlet":
            return "渲染应用主页面并准备当前登录用户等基础数据。"
        if stem == "HomeDataServlet":
            return "为首页异步请求聚合动态列表与侧栏统计数据。"
        if stem == "AdminServlet":
            return "处理后台管理页面请求并聚合管理端所需数据。"
        if stem == "TradeQrCodeServlet":
            return "根据交易地址生成用于扫码支付或确认的二维码图片。"
        base = split_java_name(stem, "Servlet")
        action = "请求处理"
        target_name = base
        for key in sorted(ACTION_WORDS, key=len, reverse=True):
            if base.endswith(key):
                target_name = base[: -len(key)]
                action = ACTION_WORDS[key]
                break
        return f"接收{domain_label(target_name)}的{action}请求，调用业务层并生成 HTTP 响应。"
    if "/util/" in rel:
        labels = {
            "HtmlUtils": "提供 HTML 特殊字符转义，降低页面输出中的注入风险。",
            "JdbcUtils": "统一创建数据库连接并处理 JDBC 资源相关基础操作。",
            "JsonUtils": "提供 JSON 字符串转义、响应输出和简单数据序列化能力。",
            "LevelUtils": "根据经验值计算用户等级、进度和升级阈值。",
            "PasswordUtils": "提供密码哈希生成与安全比对能力。",
            "SessionUtils": "统一读取和维护当前会话中的登录用户信息。",
            "TradeUrlUtils": "构造并校验站内交易流程使用的安全跳转地址。",
            "ValidationUtils": "集中提供常用文本、数字和业务参数校验方法。",
        }
        return labels.get(stem, f"提供{stem}相关的可复用辅助方法。")
    return f"实现 {stem} 对应的应用功能。"


def web_description(path: Path) -> str:
    rel = path.relative_to(ROOT).as_posix()
    if rel in EXPLICIT_DESCRIPTIONS:
        return EXPLICIT_DESCRIPTIONS[rel]
    stem = path.stem
    if path.suffix == ".css":
        return f"定义{page_label(stem)}页面或组件的布局、配色与响应式样式。"
    if path.suffix == ".jsp":
        return f"渲染{page_label(stem)}页面，输出服务端数据与前端交互所需标记。"
    if path.suffix == ".sql":
        return f"定义或迁移{page_label(stem)}相关的数据库结构和数据。"
    if path.suffix == ".xml":
        return EXPLICIT_DESCRIPTIONS.get(rel, f"配置{page_label(stem)}相关的 XML 参数。")
    return f"实现{page_label(stem)}相关功能。"


def page_label(stem: str) -> str:
    labels = {
        "activity": "活动",
        "activityDetail": "活动详情",
        "admin": "后台管理",
        "auth": "登录注册",
        "development": "功能开发提示",
        "favorites": "我的收藏",
        "forbidden": "无权限提示",
        "goodsDetail": "商品详情",
        "home": "应用首页",
        "home-feed": "首页动态流",
        "image-upload": "图片上传",
        "index": "应用主框架",
        "login": "登录",
        "lostFoundDetail": "失物招领详情",
        "lostfound": "失物招领",
        "market": "二手市场",
        "messages": "站内通知",
        "mock-pay": "模拟支付",
        "my-goods": "我的商品",
        "notice-detail": "公告详情",
        "noticeDetail": "公告详情",
        "post": "帖子",
        "post-detail": "帖子详情",
        "post-list": "帖子列表",
        "postDetail": "帖子详情",
        "private-messages": "私信",
        "privateMessageThread": "私信会话",
        "privateMessages": "私信列表",
        "profile": "个人主页",
        "public-user-profile": "公开用户主页",
        "publicUserProfile": "公开用户主页",
        "publishPost": "帖子发布",
        "purchased-goods": "已购商品",
        "register": "用户注册",
        "report": "内容举报",
        "schema": "数据库基础结构",
        "search": "全站搜索",
        "square": "校园广场",
        "trade": "商品交易",
    }
    return labels.get(stem, stem)


def read_text(path: Path) -> tuple[str, str, bool]:
    raw = path.read_bytes()
    has_bom = raw.startswith(b"\xef\xbb\xbf")
    text = raw.decode("utf-8-sig")
    newline = "\r\n" if "\r\n" in text else "\n"
    return text, newline, has_bom


def write_text(path: Path, text: str, newline: str, has_bom: bool) -> None:
    normalized = text.replace("\r\n", "\n").replace("\r", "\n")
    encoded = normalized.replace("\n", newline).encode("utf-8")
    if has_bom:
        encoded = b"\xef\xbb\xbf" + encoded
    path.write_bytes(encoded)


def annotate_java(path: Path, description: str) -> bool:
    text, newline, has_bom = read_text(path)
    marker = f" * {description}"
    if marker in text:
        return False
    declaration = re.search(
        r"(?m)^(?:public\s+)?(?:(?:abstract|final|sealed|non-sealed)\s+)?"
        r"(?:class|interface|record|enum)\s+\w+",
        text,
    )
    if not declaration:
        return False
    comment = f"/**{newline} * {description}{newline} */{newline}"
    updated = text[: declaration.start()] + comment + text[declaration.start() :]
    write_text(path, updated, newline, has_bom)
    return True


def annotate_text_file(path: Path, description: str) -> bool:
    text, newline, has_bom = read_text(path)
    if description in text[:1000]:
        return False
    suffix = path.suffix.lower()
    if suffix == ".jsp":
        comment = f"<%-- {description} --%>{newline}"
        directive_end = 0
        for match in re.finditer(r"(?m)^<%@.*?%>\s*", text):
            if match.start() == directive_end:
                directive_end = match.end()
            else:
                break
        updated = text[:directive_end] + comment + text[directive_end:]
    elif suffix == ".css":
        updated = f"/* {description} */{newline}" + text
    elif suffix == ".js":
        updated = f"// {description}{newline}" + text
    elif suffix == ".sql":
        updated = f"-- {description}{newline}" + text
    elif suffix == ".properties":
        updated = f"# {description}{newline}" + text
    elif suffix == ".xml":
        declaration = re.match(r"\s*<\?xml[^>]*\?>\s*", text)
        position = declaration.end() if declaration else 0
        comment = f"<!-- {description} -->{newline}"
        updated = text[:position] + comment + text[position:]
    else:
        return False
    write_text(path, updated, newline, has_bom)
    return True


def source_files() -> list[Path]:
    files = list((ROOT / "src").rglob("*"))
    files.extend((ROOT / "database").rglob("*"))
    files.append(ROOT / "pom.xml")
    allowed = {".java", ".js", ".jsp", ".css", ".sql", ".xml", ".properties"}
    return sorted(
        path
        for path in files
        if path.is_file()
        and path.suffix.lower() in allowed
        and "target" not in path.parts
        and ".idea" not in path.parts
    )


def file_kind(path: Path) -> str:
    rel = path.relative_to(ROOT).as_posix()
    if path.suffix == ".java":
        if "/test/" in f"/{rel}/":
            return "Java 测试"
        for part, label in {
            "/config/": "Java 配置",
            "/constant/": "Java 常量",
            "/dao/": "Java DAO",
            "/filter/": "Java 过滤器",
            "/model/": "Java 模型",
            "/service/": "Java 服务",
            "/servlet/": "Java Servlet",
            "/util/": "Java 工具",
        }.items():
            if part in rel:
                return label
        return "Java"
    return {
        ".js": "JavaScript",
        ".jsp": "JSP 视图",
        ".css": "CSS 样式",
        ".sql": "SQL",
        ".xml": "XML 配置",
        ".properties": "Properties 配置",
    }.get(path.suffix, path.suffix.lstrip(".").upper())


def generate_guide(files: list[Path]) -> None:
    lines = [
        "# 代码文件职责表",
        "",
        "本文档列出项目中的源码、视图、样式、数据库脚本和运行配置文件。",
        "图片、Markdown 说明、IDE 配置、构建产物不属于代码文件，因此不在表内。",
        "",
        "| 文件 | 类型 | 作用 |",
        "| --- | --- | --- |",
    ]
    for path in files:
        rel = path.relative_to(ROOT).as_posix()
        description = (
            EXPLICIT_DESCRIPTIONS.get(rel)
            or (java_description(path) if path.suffix == ".java" else web_description(path))
        )
        lines.append(f"| `{rel}` | {file_kind(path)} | {description} |")
    guide = ROOT / "CODE_FILES.md"
    guide.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> None:
    files = source_files()
    changed = []
    for path in files:
        rel = path.relative_to(ROOT).as_posix()
        description = (
            EXPLICIT_DESCRIPTIONS.get(rel)
            or (java_description(path) if path.suffix == ".java" else web_description(path))
        )
        if path.suffix == ".java":
            did_change = annotate_java(path, description)
        else:
            did_change = annotate_text_file(path, description)
        if did_change:
            changed.append(rel)
    generate_guide(files)
    print(f"Added Chinese responsibility comments to {len(changed)} files.")
    print(f"Generated CODE_FILES.md with {len(files)} file descriptions.")
    for rel in changed:
        print(rel)


if __name__ == "__main__":
    main()
