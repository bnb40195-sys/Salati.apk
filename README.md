# 🕌 تطبيق صلاتي (Salati) - مواقيت الصلاة والقرآن والسنة

تطبيق أندرويد متكامل لمواقيت الصلاة، القرآن والسنة، بوصلة القبلة، المسبحة الإلكترونية، وويدجت الشاشة الرئيسية، مبني بتقنيات **Jetpack Compose** و **Material Design 3**.

---

## 🚀 البناء والتحميل التلقائي عبر GitHub Actions و GitHub Releases

تم إعداد المشروع ليعمل تلقائياً مع **GitHub Actions** لبناء ملف **Debug APK** عند كل دفع (push) إلى فرع `main` / `master` أو عند إنشاء وسم إصدار (`v*`) أو يدوياً عبر `workflow_dispatch`، ونشره مباشرة في **GitHub Releases** و **Workflow Artifacts**.

### 📥 1. كيفية تحميل ملف الـ APK المباشر من GitHub:

#### الطريقة الأولى (الأسهل - رابط مباشر من Releases):
1. في صفحة مستودع GitHub الخاص بك، افتح قسم **Releases** من القائمة الجانبية.
2. ستجد أحدث إصدار منشور بعنوان: `صلاتي - Salati Android App`.
3. اضغط مباشرة على الملف لتنزيله على هاتفك:
   - 📦 `Salati-PrayerTimes-Debug.apk`
   - أو استخدم الرابط الدائم لأحدث إصدار (بعد استبدال `USERNAME/REPOSITORY_NAME`):
     `https://github.com/USERNAME/REPOSITORY_NAME/releases/latest/download/Salati-PrayerTimes-Debug.apk`

#### الطريقة الثانية: من تبويب الـ Actions (Artifacts)
1. افتح تبويب **Actions** في أعلى صفحة المستودع على GitHub.
2. اضغط على أحدث عملية بناء ناجحة (**Build & Release Android Debug APK**).
3. مرر للأسفل إلى قسم **Artifacts** وحمّل **`Salati-Debug-APK`** (ملف مضغوط `.zip` — يجب فك الضغط عنه أولاً لاستخراج ملف `.apk` قبل التثبيت).

---

## 🛠️ كيفية رفع المشروع إلى مستودع GitHub الخاص بك

```bash
# 1. تهيئة المستودع (إذا لم يكن مهيأ)
git init

# 2. إضافة جميع الملفات
git add .
git commit -m "Initial commit - Salati Android App"

# 3. ربط المستودع برابط GitHub الخاص بك
git branch -M main
git remote add origin https://github.com/USERNAME/REPOSITORY_NAME.git

# 4. رفع الكود (سيقوم GitHub Actions ببناء الـ APK ونشر Release تلقائياً!)
git push -u origin main
```

> **ملاحظة:** ملف `debug.keystore` مستبعد من Git للأمان، ويحتوي المستودع على `debug.keystore.base64` الذي يقوم الـ Workflow بفك تشفيره تلقائياً أثناء البناء لتوقيع الـ Debug APK بدون الحاجة لإضافة أي GitHub Secrets إضافية.

---

## ⚡ تشغيل البناء يدوياً (Manual Dispatch)

يمكنك تشغيل بناء ملف APK جديد ونشره في أي وقت دون الحاجة لعمل commit:
1. اذهب إلى تبويب **Actions** في GitHub.
2. اختر من القائمة اليسرى **Build & Release Android Debug APK**.
3. اضغط على **Run workflow**.
4. أبقِ خيار **Publish as GitHub Release with downloadable APK** مفعّلاً (والوسم الافتراضي `v1.0-debug` أو أي وسم تريده) ثم اضغط **Run workflow**.

---

## 📱 مميزات التطبيق

* 🕋 **مواقيت الصلاة والموقع الحقيقي**: تحديد إلزامي وحقيقي للموقع عبر GPS وعرض المدينة المكتشفة مع حساب مواقيت الصلاة بدقة.
* 🕒 **ويدجت الشاشة الرئيسية (Home Screen Widget)**: ويدجت يعمل على شاشة الهاتف ويعرض الصلاة القادمة والوقت المتبقي والتوقيت بنظام 12/24 ساعة.
* 📖 **القرآن والسنة**: تصفح المصحف الشريف مع اختيار الرواية، تكبير وتصغير حجم الخط، الاستماع للتلاوات، والأحاديث النبوية.
* 🧭 **بوصلة القبلة والتقويم الهجري والمسبحة الإلكترونية**.
* 🔐 **بوابة الحساب**: تسجيل الدخول وإنشاء الحساب قبل دخول التطبيق.

---

## 📲 تعليمات تثبيت الـ APK على هواتف Android

1. حمّل ملف `Salati-PrayerTimes-Debug.apk` إلى هاتفك.
2. اضغط على الملف لفتحه.
3. إذا ظهرت رسالة أمان: "تثبيت التطبيقات من مصادر غير معروفة" (Install unknown apps)، اختر **السماح من هذا المصدر**.
4. اضغط على **تثبيت (Install)** ثم **فتح (Open)**.

---

## ⚙️ المواصفات التقنية للبناء (CI Specifications)

* **Language**: Kotlin 2.2.10
* **UI**: Jetpack Compose (Material Design 3)
* **Android Gradle Plugin**: 9.1.1
* **Gradle Wrapper**: 9.3.1
* **Java Version**: JDK 21 (Temurin)
* **Compile / Target SDK**: 36
* **Minimum SDK**: 24 (Android 7.0+)
