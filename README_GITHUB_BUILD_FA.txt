آماده‌سازی GitHub Actions
=========================

این پروژه برای Build خودکار APK در GitHub Actions آماده شده است.

Workflow:
.github/workflows/build-apk.yml

Build:
Gradle 8.11.1 + Java 17
Task: assembleDebug

خروجی:
H&M Customs import duty calculations.apk

پس از موفقیت Build، فایل APK در بخش Artifacts اجرای Workflow قرار می‌گیرد.
