# CampusHub Database Schema Specification

`src/main/resources/database/schema.sql` is the executable source of truth for first-time deployment. This document summarizes the same structure for review and grading.

## Deployment Entry Point

```sql
SOURCE src/main/resources/database/schema.sql;
```

The script creates the `campushub` database, all current business tables, indexes, and base category data. Historical migration files remain under `database/migrations/` for existing databases, but a fresh deployment should only need the complete schema script above.

## Core Tables

| Table | Purpose | Key fields used by code |
| --- | --- | --- |
| `users` | Student/admin accounts and profile data | `username`, `password`, `nickname`, `avatar`, `student_no`, `college`, `major`, `grade`, `email`, `phone`, `role`, `status`, `experience`, `level`, `canceled_at`, `cancel_reason` |
| `categories` | Shared categories for posts, goods, lost-and-found, and activities | `name`, `type`, `description`, `sort_order`, `status` |
| `posts` | Campus square posts | `user_id`, `category_id`, `title`, `content`, `images`, `topic`, `like_count`, `comment_count`, `favorite_count`, `view_count`, `status` |
| `comments` | Post comments | `post_id`, `user_id`, `content`, `like_count`, `status` |
| `likes` | Post/comment likes | `user_id`, `target_id`, `target_type` |
| `favorites` | Favorites for posts, goods, lost-and-found, and activities | `user_id`, `target_id`, `target_type` |
| `goods` | Second-hand marketplace items | `user_id`, `category_id`, `title`, `description`, `price`, `condition_level`, `images`, `trade_place`, `trade_method`, `contact`, `status` |
| `goods_orders` | Mock online payment orders | `order_no`, `goods_id`, `buyer_id`, `seller_id`, `amount`, `pay_method`, `status`, `pay_token`, `expire_at`, `paid_at`, `cancelled_at` |
| `lost_found` | Lost-and-found records | `user_id`, `category_id`, `type`, `item_name`, `title`, `description`, `place`, `event_time`, `images`, `contact`, `status` |
| `claim_requests` | Lost-and-found claim requests | `lost_found_id`, `user_id`, `message`, `contact`, `status`, `handled_at` |
| `activities` | Campus activities | `title`, `content`, `cover_image`, `location`, `start_time`, `end_time`, `deadline`, `max_members`, `current_members`, `status`, `created_by` |
| `activity_registrations` | Activity registration records | `activity_id`, `user_id`, `status` |
| `notices` | Campus notices | `title`, `content`, `type`, `is_top`, `status`, `created_by` |
| `checkins` | Daily check-ins | `user_id`, `checkin_date`, `points`, `continuous_days` |
| `messages` | System notifications | `user_id`, `title`, `content`, `type`, `is_read` |
| `reports` | User reports for moderation | `user_id`, `target_id`, `target_type`, `reason`, `status`, `handled_by`, `handled_at` |
| `private_conversations` | Private-message conversation headers | `user_a_id`, `user_b_id`, `last_message`, `last_message_at` |
| `private_messages` | Private-message content | `conversation_id`, `sender_id`, `receiver_id`, `content`, `is_read` |
| `remember_tokens` | Remember Me login tokens | `user_id`, `selector`, `token_hash`, `expires_at`, `last_used_at`, `user_agent`, `ip_address` |
| `user_experience_logs` | Experience/level change audit trail | `user_id`, `change_value`, `source`, `description` |
| `account_cancel_logs` | Account cancellation audit trail | `user_id`, `username_snapshot`, `nickname_snapshot`, `cancel_reason`, `canceled_at`, `ip_address`, `user_agent` |

## Field Compatibility Rules

- The password column is `users.password`; there is no `password_hash` column.
- The avatar column is `users.avatar`; there is no `avatar_url` column.
- Marketplace trade mode is stored in `goods.trade_method` with values `offline`, `online`, or `both`.
- User status values are `1` for active, `0` for disabled, and `2` for canceled.
- This project intentionally does not include course-schedule tables or fields such as `course_id`, `course_name`, `classroom`, `schedule_time`, or `teacher_name`.

## Initial Category Data

The schema script seeds basic categories for:

- Posts: campus life, study discussion, club/activity topics.
- Goods: electronics, textbooks, daily supplies, sports equipment, free giveaway.
- Lost-and-found: cards/certificates, electronic devices, daily belongings.
- Activities: lectures/training, arts/sports competitions, volunteer services.
