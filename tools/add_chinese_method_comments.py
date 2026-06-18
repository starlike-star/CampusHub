"""为项目中的 Java 方法补充中文 Javadoc。

脚本使用轻量词法扫描识别类、接口、枚举和记录的直接成员方法，避免把方法调用、
控制语句或 Lambda 表达式误判为方法声明。已有 Javadoc 会原样保留。
"""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import sys


ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOTS = (ROOT / "src" / "main" / "java", ROOT / "src" / "test" / "java")

CONTROL_WORDS = {
    "if",
    "for",
    "while",
    "switch",
    "catch",
    "synchronized",
    "try",
    "return",
    "throw",
    "new",
    "assert",
    "this",
    "super",
}

PARAMETER_DESCRIPTIONS = {
    "request": "HTTP 请求对象",
    "response": "HTTP 响应对象",
    "chain": "过滤器链",
    "config": "过滤器配置",
    "servletRequest": "Servlet 请求对象",
    "servletResponse": "Servlet 响应对象",
    "id": "业务数据编号",
    "userId": "用户编号",
    "admin": "是否具有管理员权限",
    "coverImage": "封面图片地址",
    "startTime": "开始时间",
    "endTime": "结束时间",
    "deadline": "截止时间",
    "maxMembers": "人数上限",
    "currentMembers": "当前人数",
    "includeId": "是否同时设置编号",
    "createdAt": "创建时间",
    "updatedAt": "更新时间",
    "liked": "是否已点赞",
    "status": "业务状态",
    "keyword": "搜索关键字",
    "sort": "排序方式",
    "page": "页码",
    "pageSize": "每页记录数",
    "limit": "查询数量上限",
    "offset": "查询偏移量",
    "title": "标题",
    "content": "正文内容",
    "description": "描述内容",
    "username": "用户名",
    "password": "密码",
    "email": "电子邮箱",
    "nickname": "用户昵称",
    "value": "待处理的值",
    "text": "待处理文本",
    "path": "资源路径",
    "url": "目标地址",
    "connection": "数据库连接",
    "statement": "预编译 SQL 语句",
    "resultSet": "数据库查询结果集",
    "timestamp": "数据库时间戳",
    "activity": "活动数据",
    "goods": "商品数据",
    "post": "帖子数据",
    "comment": "评论数据",
    "user": "用户数据",
    "message": "消息数据",
    "order": "订单数据",
    "dao": "数据访问对象",
}

WORD_LABELS = {
    "account": "账号",
    "activity": "活动",
    "activities": "活动列表",
    "admin": "管理员",
    "all": "全部数据",
    "avatar": "头像",
    "cancel": "取消",
    "category": "分类",
    "checkin": "签到",
    "claim": "认领",
    "college": "学院",
    "comment": "评论",
    "conversation": "会话",
    "count": "数量",
    "cover": "封面",
    "create": "创建",
    "creator": "创建者",
    "current": "当前",
    "data": "数据",
    "date": "日期",
    "deadline": "截止时间",
    "detail": "详情",
    "dashboard": "后台概览",
    "editable": "可编辑",
    "email": "邮箱",
    "end": "结束",
    "experience": "经验值",
    "favorite": "收藏",
    "fake": "模拟",
    "goods": "商品",
    "grade": "年级",
    "home": "首页",
    "id": "编号",
    "image": "图片",
    "include": "包含",
    "info": "信息",
    "int": "整数",
    "keyword": "关键字",
    "level": "等级",
    "like": "点赞",
    "list": "列表",
    "location": "地点",
    "login": "登录",
    "lost": "失物",
    "max": "最大值",
    "member": "成员",
    "members": "成员",
    "message": "消息",
    "nickname": "昵称",
    "negative": "负数",
    "non": "非",
    "notice": "公告",
    "notices": "公告列表",
    "order": "订单",
    "page": "页面",
    "password": "密码",
    "post": "帖子",
    "posts": "帖子列表",
    "profile": "个人资料",
    "public": "公开",
    "read": "已读状态",
    "registration": "报名",
    "registrations": "报名记录",
    "registered": "已报名",
    "remember": "记住登录",
    "report": "举报",
    "reports": "举报记录",
    "result": "结果",
    "search": "搜索",
    "session": "会话",
    "sort": "排序方式",
    "start": "开始",
    "status": "状态",
    "time": "时间",
    "title": "标题",
    "token": "令牌",
    "trade": "交易",
    "unread": "未读",
    "update": "更新",
    "url": "地址",
    "user": "用户",
    "users": "用户列表",
    "username": "用户名",
    "value": "值",
}


@dataclass(frozen=True)
class MethodDeclaration:
    """保存一个 Java 方法声明及其注释插入位置。"""

    insert_at: int
    indent: str
    owner: str
    name: str
    return_type: str | None
    parameters: tuple[str, ...]
    thrown_types: tuple[str, ...]
    is_constructor: bool


def read_text(path: Path) -> tuple[str, str, bool]:
    """读取 UTF-8 文件并保留换行符及 BOM 信息。"""
    raw = path.read_bytes()
    has_bom = raw.startswith(b"\xef\xbb\xbf")
    text = raw.decode("utf-8-sig")
    newline = "\r\n" if "\r\n" in text else "\n"
    return text, newline, has_bom


def write_text(path: Path, text: str, newline: str, has_bom: bool) -> None:
    """按原文件编码特征写回文本。"""
    normalized = text.replace("\r\n", "\n").replace("\r", "\n")
    encoded = normalized.replace("\n", newline).encode("utf-8")
    if has_bom:
        encoded = b"\xef\xbb\xbf" + encoded
    path.write_bytes(encoded)


def mask_non_code(text: str) -> str:
    """屏蔽注释和字符串，同时保留字符位置与换行。"""
    chars = list(text)
    index = 0
    state = "code"
    while index < len(chars):
        current = chars[index]
        following = chars[index + 1] if index + 1 < len(chars) else ""
        if state == "code":
            if current == "/" and following == "/":
                chars[index] = chars[index + 1] = " "
                index += 2
                state = "line_comment"
                continue
            if current == "/" and following == "*":
                chars[index] = chars[index + 1] = " "
                index += 2
                state = "block_comment"
                continue
            if text.startswith('"""', index):
                chars[index:index + 3] = [" ", " ", " "]
                index += 3
                state = "text_block"
                continue
            if current == '"':
                chars[index] = " "
                index += 1
                state = "string"
                continue
            if current == "'":
                chars[index] = " "
                index += 1
                state = "character"
                continue
        elif state == "line_comment":
            if current == "\n":
                state = "code"
            else:
                chars[index] = " "
        elif state == "block_comment":
            if current == "*" and following == "/":
                chars[index] = chars[index + 1] = " "
                index += 2
                state = "code"
                continue
            if current not in "\r\n":
                chars[index] = " "
        elif state == "text_block":
            if text.startswith('"""', index):
                chars[index:index + 3] = [" ", " ", " "]
                index += 3
                state = "code"
                continue
            if current not in "\r\n":
                chars[index] = " "
        else:
            if current == "\\":
                chars[index] = " "
                if index + 1 < len(chars) and chars[index + 1] not in "\r\n":
                    chars[index + 1] = " "
                    index += 2
                    continue
            elif (state == "string" and current == '"') or (
                    state == "character" and current == "'"
            ):
                chars[index] = " "
                state = "code"
            elif current not in "\r\n":
                chars[index] = " "
        index += 1
    return "".join(chars)


def brace_depths(masked: str) -> list[int]:
    """计算每个字符之前所在的大括号深度。"""
    depths = [0] * (len(masked) + 1)
    depth = 0
    for index, char in enumerate(masked):
        depths[index] = depth
        if char == "{":
            depth += 1
        elif char == "}":
            depth = max(0, depth - 1)
    depths[len(masked)] = depth
    return depths


def matching_parenthesis(masked: str, opening: int) -> int | None:
    """查找与指定左括号对应的右括号。"""
    depth = 0
    for index in range(opening, len(masked)):
        if masked[index] == "(":
            depth += 1
        elif masked[index] == ")":
            depth -= 1
            if depth == 0:
                return index
    return None


def matching_brace(masked: str, opening: int) -> int | None:
    """查找与指定左大括号对应的右大括号。"""
    depth = 0
    for index in range(opening, len(masked)):
        if masked[index] == "{":
            depth += 1
        elif masked[index] == "}":
            depth -= 1
            if depth == 0:
                return index
    return None


def next_declaration_terminator(masked: str, start: int) -> tuple[int, str] | None:
    """定位参数列表后的方法体左括号或接口方法分号。"""
    angle_depth = 0
    square_depth = 0
    index = start
    while index < len(masked):
        char = masked[index]
        if char == "<":
            angle_depth += 1
        elif char == ">" and angle_depth:
            angle_depth -= 1
        elif char == "[":
            square_depth += 1
        elif char == "]" and square_depth:
            square_depth -= 1
        elif angle_depth == 0 and square_depth == 0 and char in "{;":
            return index, char
        elif angle_depth == 0 and square_depth == 0 and char == "=":
            return None
        index += 1
    return None


def split_top_level(value: str) -> list[str]:
    """按顶层逗号拆分泛型或注解参数。"""
    parts: list[str] = []
    start = 0
    depths = {"<": 0, "(": 0, "[": 0}
    pairs = {">": "<", ")": "(", "]": "["}
    for index, char in enumerate(value):
        if char in depths:
            depths[char] += 1
        elif char in pairs and depths[pairs[char]]:
            depths[pairs[char]] -= 1
        elif char == "," and not any(depths.values()):
            parts.append(value[start:index])
            start = index + 1
    parts.append(value[start:])
    return parts


def parameter_names(parameter_text: str) -> tuple[str, ...]:
    """从 Java 参数列表中提取参数名。"""
    names: list[str] = []
    for part in split_top_level(parameter_text):
        cleaned = re.sub(r"@\w+(?:\s*\([^)]*\))?\s*", "", part).strip()
        cleaned = re.sub(r"\bfinal\b\s*", "", cleaned)
        match = re.search(r"([A-Za-z_$][\w$]*)\s*(?:\[\s*])?\s*$", cleaned)
        if match:
            names.append(match.group(1))
    return tuple(names)


def thrown_type_names(tail: str) -> tuple[str, ...]:
    """提取 throws 子句中的异常类型。"""
    match = re.search(r"\bthrows\s+(.+)$", tail, re.DOTALL)
    if not match:
        return ()
    names = []
    for item in split_top_level(match.group(1)):
        simple_name = item.strip().split(".")[-1]
        if re.fullmatch(r"[A-Za-z_$][\w$]*", simple_name):
            names.append(simple_name)
    return tuple(names)


def declaration_start(text: str, masked: str, name_start: int, member_depth: int,
                      depths: list[int]) -> int:
    """确定方法声明起点，并包含紧邻方法的注解行。"""
    del masked, member_depth, depths
    line_start = text.rfind("\n", 0, name_start) + 1
    while line_start > 0:
        previous_end = line_start - 1
        previous_start = text.rfind("\n", 0, previous_end) + 1
        previous_line = text[previous_start:previous_end].strip()
        if previous_line.startswith("@"):
            line_start = previous_start
        else:
            break
    return line_start


def has_javadoc_before(text: str, position: int) -> bool:
    """判断声明前是否已经存在紧邻的 Javadoc。"""
    prefix = text[:position].rstrip()
    if not prefix.endswith("*/"):
        return False
    opening = prefix.rfind("/**")
    closing = prefix.rfind("*/")
    return opening >= 0 and opening < closing


def find_type_bodies(
        masked: str,
        depths: list[int],
) -> list[tuple[int, int, int, str]]:
    """查找类型体的成员深度和类型名称。"""
    bodies: list[tuple[int, int, int, str]] = []
    pattern = re.compile(r"\b(?:class|interface|record|enum)\s+([A-Za-z_$][\w$]*)")
    for match in pattern.finditer(masked):
        opening = masked.find("{", match.end())
        closing = matching_brace(masked, opening) if opening >= 0 else None
        if opening >= 0 and closing is not None:
            bodies.append((
                depths[opening] + 1,
                opening,
                closing,
                match.group(1),
            ))
    return bodies


def return_type_from_prefix(prefix: str, name: str, owner: str) -> tuple[str | None, bool]:
    """从声明前缀推断返回类型或构造器。"""
    if name == owner:
        return None, True
    cleaned = re.sub(r"@\w+(?:\s*\([^)]*\))?\s*", " ", prefix)
    cleaned = re.sub(
        r"\b(?:public|protected|private|static|final|abstract|synchronized|"
        r"native|strictfp|default|transient)\b",
        " ",
        cleaned,
    )
    cleaned = re.sub(r"^\s*<[^>]+>\s*", "", cleaned.strip(), flags=re.DOTALL)
    tokens = re.findall(r"[A-Za-z_$][\w$]*(?:\s*<[^;{}()]+>)?(?:\s*\[\s*])?", cleaned)
    return (tokens[-1].replace(" ", "") if tokens else "Object"), False


def find_methods(
        text: str,
        include_documented: bool = False,
) -> list[MethodDeclaration]:
    """识别文本中缺少 Javadoc 的 Java 方法声明。"""
    masked = mask_non_code(text)
    depths = brace_depths(masked)
    type_bodies = find_type_bodies(masked, depths)
    methods: list[MethodDeclaration] = []
    for opening_match in re.finditer(r"\(", masked):
        opening = opening_match.start()
        owners = [
            (body_opening, owner)
            for depth, body_opening, body_closing, owner in type_bodies
            if depth == depths[opening] and body_opening < opening < body_closing
        ]
        if not owners:
            continue
        name_match = re.search(r"([A-Za-z_$][\w$]*)\s*$", masked[:opening])
        if not name_match:
            continue
        name = name_match.group(1)
        if name in CONTROL_WORDS:
            continue
        closing = matching_parenthesis(masked, opening)
        if closing is None:
            continue
        terminator = next_declaration_terminator(masked, closing + 1)
        if terminator is None:
            continue
        terminator_at, terminator_char = terminator
        if depths[terminator_at] != depths[opening]:
            continue
        between = masked[closing + 1:terminator_at].strip()
        if between.startswith("->") or re.search(r"\b(?:new|return)\b", between):
            continue
        start = declaration_start(
            text,
            masked,
            name_match.start(),
            depths[opening],
            depths,
        )
        prefix = masked[start:name_match.start()]
        if "=" in prefix or "->" in prefix:
            continue
        if not re.search(
                r"\b(?:public|protected|private|static|final|abstract|"
                r"synchronized|native|strictfp|default|<[\w?,\s extends super.&]+>|"
                r"[A-Za-z_$][\w$<>,.?\[\]\s]*)\s+$",
                prefix,
            ):
            continue
        if terminator_char == ";" and re.search(r"\b(?:return|throw|new)\b", prefix):
            continue
        if not include_documented and has_javadoc_before(text, start):
            continue
        line = text[start:text.find("\n", start) if "\n" in text[start:] else len(text)]
        indent = re.match(r"\s*", line).group(0)
        owner = max(owners)[1]
        return_type, is_constructor = return_type_from_prefix(prefix, name, owner)
        methods.append(MethodDeclaration(
            insert_at=start,
            indent=indent,
            owner=owner,
            name=name,
            return_type=return_type,
            parameters=parameter_names(text[opening + 1:closing]),
            thrown_types=thrown_type_names(masked[closing + 1:terminator_at].strip()),
            is_constructor=is_constructor,
        ))
    unique = {method.insert_at: method for method in methods}
    return sorted(unique.values(), key=lambda method: method.insert_at)


def camel_words(name: str) -> list[str]:
    """将驼峰标识符拆分为小写单词。"""
    return [
        word.lower()
        for word in re.findall(
            r"[A-Z]+(?=[A-Z][a-z]|\d|\b)|[A-Z]?[a-z]+|\d+",
            name,
        )
    ]


def label_for_name(name: str) -> str:
    """把标识符转换为适合中文注释的业务标签。"""
    words = camel_words(name)
    if not words or any(word not in WORD_LABELS for word in words):
        return f"`{name}`"
    return "".join(WORD_LABELS[word] for word in words)


def method_summary(method: MethodDeclaration, path: Path) -> str:
    """根据方法名和所在层次生成中文职责说明。"""
    name = method.name
    owner_label = label_for_name(re.sub(
        r"(?:Dao|Service|Servlet|Filter|Utils?|Test)$",
        "",
        method.owner,
    ))
    if method.is_constructor:
        return f"初始化{owner_label}对象及其运行所需依赖。"
    if (
            "/test/" in f"/{path.relative_to(ROOT).as_posix()}/"
            and method.owner.endswith("Test")
    ):
        return f"验证 `{name}` 场景下的业务行为与预期结果一致。"
    if name == "doGet":
        return f"处理{owner_label}相关的 HTTP GET 请求并生成响应。"
    if name == "doPost":
        return f"处理{owner_label}相关的 HTTP POST 请求并生成响应。"
    if name in {"doFilter", "doFilterInternal"}:
        return f"对{owner_label}相关请求执行前置校验并决定是否继续过滤器链。"
    if name in {"init", "initialize"}:
        return f"初始化{owner_label}运行所需的资源和配置。"
    if name in {"destroy", "close"}:
        return f"释放{owner_label}持有的资源。"
    if name in {"success", "completed"}:
        return "创建表示操作成功的结果对象。"
    if name in {"failure", "failed"}:
        return "创建表示操作失败的结果对象。"
    if name == "detail":
        return f"查询{owner_label}详情。"
    if name.startswith("findBy"):
        condition = label_for_name(name[len("findBy"):])
        return f"根据{condition}查询{owner_label}。"
    if name.startswith("populateAndValidate"):
        target = name[len("populateAndValidate"):]
        target_label = label_for_name(target) if target else owner_label
        return f"填充并校验{target_label}数据。"
    if name == "registeredActivityIds":
        return "查询当前用户已报名的活动编号集合。"
    prefixes = [
        ("get", "获取"),
        ("set", "设置"),
        ("is", "判断是否"),
        ("has", "判断是否具有"),
        ("can", "判断是否可以"),
        ("should", "判断是否需要"),
        ("findAll", "查询全部"),
        ("find", "查询"),
        ("list", "查询"),
        ("search", "搜索"),
        ("count", "统计"),
        ("register", "提交"),
        ("cancel", "取消"),
        ("create", "创建"),
        ("add", "新增"),
        ("insert", "新增"),
        ("save", "保存"),
        ("update", "更新"),
        ("reset", "重置"),
        ("reject", "驳回"),
        ("approve", "通过审核"),
        ("notify", "发送通知："),
        ("delete", "删除"),
        ("remove", "移除"),
        ("toggle", "切换"),
        ("increment", "增加"),
        ("decrement", "减少"),
        ("mark", "标记"),
        ("read", "读取"),
        ("write", "写入"),
        ("parse", "解析"),
        ("normalize", "规范化"),
        ("validate", "校验"),
        ("check", "检查"),
        ("map", "将数据库结果映射为"),
        ("build", "构建"),
        ("to", "转换为"),
        ("from", "根据输入创建"),
        ("calculate", "计算"),
        ("compute", "计算"),
        ("generate", "生成"),
        ("encode", "编码"),
        ("escape", "转义"),
        ("send", "发送"),
        ("handle", "处理"),
        ("load", "加载"),
        ("truncate", "按长度限制截断"),
        ("current", "获取当前"),
    ]
    for prefix, action in sorted(prefixes, key=lambda item: len(item[0]), reverse=True):
        if name.startswith(prefix):
            target = name[len(prefix):]
            target_label = label_for_name(target) if target else owner_label
            return f"{action}{target_label}。"
    return_type = method.return_type or ""
    if any(
            container in return_type
            for container in ("List", "Set", "Collection", "Map", "Optional")
    ):
        return f"查询{label_for_name(name)}并返回结果。"
    if return_type not in {"", "void"} and not method.parameters:
        return f"获取{label_for_name(name)}。"
    if return_type not in {"", "void"}:
        return f"根据输入计算并返回 `{name}` 的处理结果。"
    return f"处理 `{name}` 对应的业务流程。"


def parameter_description(name: str) -> str:
    """生成方法参数的中文说明。"""
    if name in PARAMETER_DESCRIPTIONS:
        return PARAMETER_DESCRIPTIONS[name]
    if name.endswith("Id"):
        return f"{label_for_name(name[:-2])}编号"
    if name.startswith("is") and len(name) > 2:
        return f"是否{label_for_name(name[2:])}"
    return f"参数 `{name}`"


def return_description(method: MethodDeclaration) -> str | None:
    """根据返回类型和方法名生成返回值说明。"""
    return_type = method.return_type or ""
    if method.is_constructor or return_type == "void":
        return None
    boolean_name = re.match(r"^(?:is|has|can)[A-Z_]", method.name) is not None
    if return_type in {"boolean", "Boolean"} or boolean_name:
        return "满足条件或操作成功时返回 true，否则返回 false"
    if "Optional" in return_type:
        return "查询到的数据；不存在时返回空结果"
    if any(container in return_type for container in ("List", "Set", "Collection")):
        return "符合条件的数据列表"
    if "Map" in return_type:
        return "按键组织的结果数据"
    if "ServiceResult" in return_type:
        return "包含处理状态、提示信息和业务数据的结果"
    if method.name.startswith(("create", "insert")) and return_type in {
            "long", "Long", "int", "Integer"
    }:
        return "新建数据的编号"
    if method.name.startswith("get"):
        target = method.name[3:]
        return label_for_name(target) if target else "读取到的配置值"
    if method.name.startswith("parse"):
        return "解析后的值；输入无效时返回 null"
    if not method.parameters:
        return label_for_name(method.name)
    return "方法处理结果"


def throws_description(exception_type: str) -> str:
    """生成异常标签说明。"""
    descriptions = {
        "SQLException": "数据库访问失败时抛出",
        "IOException": "读取请求或写入响应失败时抛出",
        "ServletException": "Servlet 处理请求失败时抛出",
        "NoSuchAlgorithmException": "运行环境不支持所需算法时抛出",
    }
    return descriptions.get(exception_type, "处理过程中发生该异常时抛出")


def render_javadoc(method: MethodDeclaration, path: Path, newline: str) -> str:
    """渲染一个方法的完整中文 Javadoc。"""
    lines = [
        f"{method.indent}/**",
        f"{method.indent} * {method_summary(method, path)}",
    ]
    if method.parameters or return_description(method) or method.thrown_types:
        lines.append(f"{method.indent} *")
    for parameter in method.parameters:
        lines.append(
            f"{method.indent} * @param {parameter} {parameter_description(parameter)}"
        )
    result_description = return_description(method)
    if result_description:
        lines.append(f"{method.indent} * @return {result_description}")
    for exception_type in method.thrown_types:
        lines.append(
            f"{method.indent} * @throws {exception_type} "
            f"{throws_description(exception_type)}"
        )
    lines.append(f"{method.indent} */")
    return newline.join(lines) + newline


def annotate_file(path: Path) -> int:
    """为单个 Java 文件中缺少注释的方法补充 Javadoc。"""
    text, newline, has_bom = read_text(path)
    methods = find_methods(text)
    if not methods:
        return 0
    updated = text
    for method in reversed(methods):
        comment = render_javadoc(method, path, newline)
        updated = updated[:method.insert_at] + comment + updated[method.insert_at:]
    write_text(path, updated, newline, has_bom)
    return len(methods)


def refresh_generic_summaries(path: Path) -> int:
    """刷新早期生成的通用方法摘要，不改动人工编写的 Javadoc。"""
    text, newline, has_bom = read_text(path)
    replacements: list[tuple[int, int, str]] = []
    generic_pattern = re.compile(
        r"(?m)^(\s*\* )执行.*的 `([A-Za-z_$][\w$]*)` 业务处理。\s*$"
    )
    for method in find_methods(text, include_documented=True):
        prefix = text[max(0, method.insert_at - 2000):method.insert_at]
        closing = prefix.rfind("*/")
        opening = prefix.rfind("/**", 0, closing + 1)
        if opening < 0 or closing < opening:
            continue
        comment_start = method.insert_at - len(prefix) + opening
        comment_end = method.insert_at - len(prefix) + closing + 2
        between = text[comment_end:method.insert_at]
        if re.sub(r"(?m)^\s*@\w+(?:\([^)]*\))?\s*$", "", between).strip():
            continue
        comment = text[comment_start:comment_end]
        match = generic_pattern.search(comment)
        if not match or match.group(2) != method.name:
            continue
        summary = f"{match.group(1)}{method_summary(method, path)}"
        replacements.append((
            comment_start + match.start(),
            comment_start + match.end(),
            summary,
        ))
    if not replacements:
        return 0
    updated = text
    for start, end, replacement in reversed(replacements):
        updated = updated[:start] + replacement + updated[end:]
    write_text(path, updated, newline, has_bom)
    return len(replacements)


def rewrite_generated_method_javadocs(path: Path) -> int:
    """显式维护模式下重写生成的方法 Javadoc。"""
    text, newline, has_bom = read_text(path)
    replacements: list[tuple[int, int, str]] = []
    for method in find_methods(text, include_documented=True):
        prefix = text[max(0, method.insert_at - 3000):method.insert_at]
        closing = prefix.rfind("*/")
        opening = prefix.rfind("/**", 0, closing + 1)
        if opening < 0 or closing < opening:
            continue
        opening_at = method.insert_at - len(prefix) + opening
        comment_start = text.rfind("\n", 0, opening_at) + 1
        comment_end = method.insert_at - len(prefix) + closing + 2
        between = text[comment_end:method.insert_at]
        if re.sub(r"(?m)^\s*@\w+(?:\([^)]*\))?\s*$", "", between).strip():
            continue
        replacement = render_javadoc(method, path, newline).rstrip("\r\n")
        if text[comment_start:comment_end] != replacement:
            replacements.append((comment_start, comment_end, replacement))
    if not replacements:
        return 0
    updated = text
    for start, end, replacement in reversed(replacements):
        updated = updated[:start] + replacement + updated[end:]
    write_text(path, updated, newline, has_bom)
    return len(replacements)


def main() -> None:
    """扫描项目 Java 源码并输出注释补充统计。"""
    changed_files = 0
    changed_methods = 0
    refreshed_summaries = 0
    rewritten_javadocs = 0
    rewrite_generated = "--rewrite-generated" in sys.argv[1:]
    for java_root in JAVA_ROOTS:
        if not java_root.exists():
            continue
        for path in sorted(java_root.rglob("*.java")):
            count = annotate_file(path)
            refreshed = refresh_generic_summaries(path)
            rewritten = (
                rewrite_generated_method_javadocs(path)
                if rewrite_generated
                else 0
            )
            if count:
                changed_files += 1
                changed_methods += count
                print(f"{path.relative_to(ROOT).as_posix()}: {count}")
            refreshed_summaries += refreshed
            rewritten_javadocs += rewritten
    print(
        f"Added Chinese Javadoc to {changed_methods} methods "
        f"across {changed_files} files."
    )
    print(f"Refreshed {refreshed_summaries} generic method summaries.")
    if rewrite_generated:
        print(f"Rewrote {rewritten_javadocs} generated method Javadocs.")


if __name__ == "__main__":
    main()
