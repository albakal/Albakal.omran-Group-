# دليل النشر والرفع على Google Play Console خطوة بخطوة

تم تجهيز حزمة تطبيق **البقال** الرسمية Android App Bundle (`.aab`) وكافة متطلبات المتجر لتتمكن من رفعها وإطلاق التطبيق على Google Play Console بسهولة:

---

## 🚀 خطوات رفع التطبيق على حساب Google Play Console

### الخطوة 1: إنشاء تطبيق جديد (Create App)
1. سجل الدخول إلى لوحة التحكم: [Google Play Console](https://play.google.com/console).
2. اضغط على زر **Create app** (إنشاء تطبيق).
3. أدخل البيانات الأساسية:
   - **App name:** `البقال - تسوق وطلبات البقالة`
   - **Default language:** العربية - Arabic (`ar`).
   - **App or game:** App (تطبيق).
   - **Free or paid:** Free (مجاني).
4. وافق على شروط وإقرارات Google ثم اضغط **Create app**.

---

### الخطوة 2: بطاقة المتجر الرئيسية (Main Store Listing)
من القائمة الجانبية، انتقل إلى **Grow** > **Store presence** > **Main store listing**:
1. الصق النصوص المجهزة من ملف `play-store-kit/STORE_LISTING_AR.md`:
   - **App name:** البقال - تسوق وطلبات البقالة
   - **Short description:** تطبيق البقال لربط المتاجر والعملاء وخدمات التوصيل السريع داخل منطقتك.
   - **Full description:** (انسخ النص الكامل من الملف).
2. ارفع الصور الجاهزة في المشروع:
   - **App icon:** ارفع ملف `play-store-kit/icon_512x512.png`.
   - **Feature graphic:** ارفع ملف `play-store-kit/feature_graphic_1024x500.png`.
   - **Phone screenshots:** التقط 2 إلى 4 لقطات شاشة للتطبيق أو ارفع الصور التوضيحية.
3. اضغط **Save**.

---

### الخطوة 3: سياسة الخصوصية وتصنيف المحتوى (App Content & Policy)
من القائمة الجانبية، انتقل إلى **Policy and programs** > **App content**:
1. **Privacy Policy (سياسة الخصوصية):**
   - ضع الرابط المباشر المستضاف على GitHub Pages:
     `https://albakal.github.io/Albakal.omran-Group-/privacy.html`
2. **Ads:** اختر "No, my app does not contain ads".
3. **App access:** اختر "All functionality is available without special access".
4. **Target audience:** اختر 18 فما فوق.
5. **Content rating:** أكمل استبيان تصنيف المحتوى البسيط.

---

### الخطوة 4: رفع حزمة التطبيق (Upload Bundle)
1. من القائمة الجانبية، انتقل إلى **Release** > **Production** (أو **Internal testing** للتجربة الفورية).
2. اضغط **Create new release**.
3. اسحب وأفلت ملف الحزمة الموقعة:
   `bundle/albakal-release.aab`
4. اكتب اسم الإصدار (Release name): `1.0.0 (1)`.
5. اضغط **Next** ثم **Save and publish** لإرسال التطبيق للمراجعة.

---

## 🔗 رابط التطبيق الرسمي على متجر Google Play
بمجرد اعتماد التطبيق من فريق مراجعة جوجل، سيكون رابط التطبيق المباشر لجميع المستخدمين حول العالم هو:
```text
https://play.google.com/store/apps/details?id=com.albakal.app
```
(حيث `com.albakal.app` هو معرف الحزمة المعتمد في التطبيق).
