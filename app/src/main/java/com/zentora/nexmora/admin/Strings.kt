package com.zentora.nexmora.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

enum class Lang(val code: String, val label: String) {
    HI("hi", "हिंदी"),
    BN("bn", "বাংলা"),
    EN("en", "English");

    companion object {
        fun from(code: String?): Lang = entries.firstOrNull { it.code == code } ?: HI
    }
}

class Tx(val hi: String, val bn: String, val en: String) {
    fun get(lang: Lang): String = when (lang) {
        Lang.HI -> hi
        Lang.BN -> bn
        Lang.EN -> en
    }
}

val LocalLang = compositionLocalOf { Lang.HI }

@Composable
fun Tx.t(): String = get(LocalLang.current)

object S {
    val connectTitle = Tx("सर्वर से जुड़ें", "সার্ভারের সাথে যুক্ত হন", "Connect to server")
    val connectHint = Tx(
        "अपने Render सर्वर का पता और Admin Token डालें। ये सिर्फ़ इसी फ़ोन में सुरक्षित रहेंगे।",
        "আপনার Render সার্ভারের ঠিকানা ও Admin Token দিন। এগুলো শুধু এই ফোনেই সুরক্ষিত থাকবে।",
        "Enter your Render server address and Admin Token. They stay protected on this phone only."
    )
    val serverAddress = Tx("सर्वर का पता", "সার্ভারের ঠিকানা", "Server address")
    val adminToken = Tx("Admin Token", "Admin Token", "Admin Token")
    val checkConnect = Tx("जाँचें और आगे बढ़ें", "যাচাই করে এগিয়ে যান", "Check and continue")
    val waking = Tx(
        "सर्वर सो रहा हो सकता है, एक मिनट तक लग सकता है…",
        "সার্ভার ঘুমিয়ে থাকতে পারে, এক মিনিট পর্যন্ত লাগতে পারে…",
        "The server may be asleep; this can take up to a minute…"
    )

    val secureTitle = Tx("फिंगरप्रिंट और पासवर्ड सेट करें", "ফিঙ্গারপ্রিন্ট ও পাসওয়ার্ড সেট করুন", "Set fingerprint and password")
    val secureHint = Tx(
        "यह ऐप सिर्फ़ आपकी उँगली से खुलेगा। डिलीट या बैन जैसे काम के लिए यह डिलीट पासवर्ड हर बार पूछा जाएगा (कम से कम 8 अक्षर)।",
        "এই অ্যাপ শুধু আপনার আঙুলে খুলবে। ডিলিট বা ব্যান করার সময় প্রতিবার এই ডিলিট পাসওয়ার্ড চাওয়া হবে (কমপক্ষে ৮ অক্ষর)।",
        "This app opens only with your finger. This delete password is asked every time you delete or ban (at least 8 characters)."
    )
    val deletePassword = Tx("डिलीट पासवर्ड", "ডিলিট পাসওয়ার্ড", "Delete password")
    val repeatPassword = Tx("पासवर्ड दोबारा", "পাসওয়ার্ড আবার", "Repeat password")
    val setFinger = Tx("फिंगरप्रिंट सेट करें और पूरा करें", "ফিঙ্গারপ্রিন্ট সেট করে শেষ করুন", "Set fingerprint and finish")
    val back = Tx("वापस", "ফিরে যান", "Back")

    val recoveryTitle = Tx("आपका 25 अंकों का कोड", "আপনার ২৫ সংখ্যার কোড", "Your 25-digit code")
    val recoveryHint = Tx(
        "इसे कागज़ पर लिखकर सुरक्षित रखें। फ़ोन बदलने या फिंगरप्रिंट बदलने पर सिर्फ़ यही कोड काम आएगा। यह दोबारा नहीं दिखेगा।",
        "এটি কাগজে লিখে নিরাপদে রাখুন। ফোন বা ফিঙ্গারপ্রিন্ট বদলালে শুধু এই কোডই কাজে লাগবে। এটি আর দেখানো হবে না।",
        "Write it on paper and keep it safe. If you change phone or fingerprint, only this code will work. It will not be shown again."
    )
    val recoveryWritten = Tx("मैंने कोड लिख लिया है", "আমি কোড লিখে নিয়েছি", "I have written the code down")
    val continueBtn = Tx("आगे बढ़ें", "এগিয়ে যান", "Continue")

    val lockTitle = Tx("Nexmora Admin लॉक है", "Nexmora Admin লক করা আছে", "Nexmora Admin is locked")
    val unlock = Tx("फिंगरप्रिंट से खोलें", "ফিঙ্গারপ্রিন্টে খুলুন", "Unlock with fingerprint")
    val lostFinger = Tx(
        "फिंगरप्रिंट या फ़ोन बदला? 25 अंकों के कोड से खोलें",
        "ফিঙ্গারপ্রিন্ট বা ফোন বদলেছে? ২৫ সংখ্যার কোড দিয়ে খুলুন",
        "Fingerprint or phone changed? Use the 25-digit code"
    )

    val rebindTitle = Tx("25 अंकों के कोड से नया फिंगरप्रिंट", "২৫ সংখ্যার কোড দিয়ে নতুন ফিঙ্গারপ্রিন্ট", "New fingerprint with the 25-digit code")
    val rebindHint = Tx(
        "सही कोड डालने पर यह फ़ोन और आपकी नई उँगली इस सर्वर से जुड़ जाएगी, और आपको नया 25 अंकों का कोड मिलेगा।",
        "সঠিক কোড দিলে এই ফোন ও আপনার নতুন আঙুল সার্ভারের সাথে যুক্ত হবে, এবং আপনি নতুন ২৫ সংখ্যার কোড পাবেন।",
        "With the correct code this phone and your new finger are bound to the server, and you get a new 25-digit code."
    )
    val codeField = Tx("25 अंकों का कोड", "২৫ সংখ্যার কোড", "25-digit code")
    val rebindBtn = Tx("कोड जाँचें और नया फिंगरप्रिंट जोड़ें", "কোড যাচাই করে নতুন ফিঙ্গারপ্রিন্ট যোগ করুন", "Check code and add new fingerprint")

    val tabOverview = Tx("ओवरव्यू", "ওভারভিউ", "Overview")
    val tabSettings = Tx("सेटिंग", "সেটিং", "Settings")
    val refresh = Tx("रिफ्रेश", "রিফ্রেশ", "Refresh")
    val updatedAt = Tx("आख़िरी अपडेट", "সর্বশেষ আপডেট", "Last updated")
    val retry = Tx("फिर कोशिश करें", "আবার চেষ্টা করুন", "Try again")

    val stUsers = Tx("कुल यूज़र", "মোট ইউজার", "Total users")
    val stNew = Tx("नए यूज़र (24 घंटे)", "নতুন ইউজার (২৪ ঘণ্টা)", "New users (24 h)")
    val stVideos = Tx("वीडियो", "ভিডিও", "Videos")
    val stComments = Tx("कमेंट", "কমেন্ট", "Comments")
    val stMedia = Tx("मीडिया फ़ाइलें", "মিডিয়া ফাইল", "Media files")
    val stStorage = Tx("मीडिया स्टोरेज", "মিডিয়া স্টোরেজ", "Media storage")
    val stAds = Tx("विज्ञापन", "বিজ্ঞাপন", "Ads")
    val stFriends = Tx("दोस्ती के रिश्ते", "বন্ধুত্বের সম্পর্ক", "Friendships")
    val stRequests = Tx("लंबित रिक्वेस्ट", "অপেক্ষমাণ রিকোয়েস্ট", "Pending requests")
    val stChats = Tx("चैट संदेश", "চ্যাট বার্তা", "Chat messages")
    val stCalls = Tx("कॉल", "কল", "Calls")
    val stLedger = Tx("लेजर ब्लॉक", "লেজার ব্লক", "Ledger blocks")
    val stBanned = Tx("बैन यूज़र", "ব্যান ইউজার", "Banned users")
    val stDelAdmin = Tx("आपने डिलीट किए", "আপনি ডিলিট করেছেন", "Deleted by you")
    val stDelUser = Tx("यूज़र ने खुद डिलीट किए", "ইউজার নিজে ডিলিট করেছে", "Deleted by users")
    val stPurged = Tx("हमेशा के लिए मिटे", "চিরতরে মুছে গেছে", "Permanently purged")
    val stDb = Tx("डेटाबेस का आकार", "ডেটাবেসের আকার", "Database size")

    val serverSection = Tx("सर्वर", "সার্ভার", "Server")
    val uptime = Tx("चालू रहने का समय", "চালু থাকার সময়", "Uptime")
    val memory = Tx("मेमोरी", "মেমোরি", "Memory")
    val nodeVersion = Tx("Node संस्करण", "Node সংস্করণ", "Node version")

    val language = Tx("भाषा", "ভাষা", "Language")
    val screenProtect = Tx("स्क्रीनशॉट और रिकॉर्डिंग रोकें", "স্ক্রিনশট ও রেকর্ডিং বন্ধ রাখুন", "Block screenshots and recording")
    val screenProtectHint = Tx(
        "चालू रखने की सलाह है, क्योंकि स्क्रीन पर यूज़र का निजी डेटा दिख सकता है।",
        "চালু রাখার পরামর্শ, কারণ স্ক্রিনে ইউজারের ব্যক্তিগত ডেটা দেখা যেতে পারে।",
        "Keeping it on is recommended because private user data can appear on screen."
    )
    val lockNow = Tx("अभी लॉक करें", "এখনই লক করুন", "Lock now")
    val connectedTo = Tx("जुड़ा हुआ सर्वर", "যুক্ত সার্ভার", "Connected server")
    val version = Tx("संस्करण", "সংস্করণ", "Version")
    val codeNote = Tx(
        "फिंगरप्रिंट बदलना हो तो ऐप लॉक करें और \"25 अंकों के कोड से खोलें\" चुनें।",
        "ফিঙ্গারপ্রিন্ট বদলাতে অ্যাপ লক করুন ও \"২৫ সংখ্যার কোড দিয়ে খুলুন\" বেছে নিন।",
        "To change the fingerprint, lock the app and choose \"Use the 25-digit code\"."
    )

    val promptTitle = Tx("Nexmora Admin", "Nexmora Admin", "Nexmora Admin")
    val promptSub = Tx("अपनी उँगली लगाएँ", "আপনার আঙুল লাগান", "Place your finger")
    val promptCancel = Tx("रद्द करें", "বাতিল", "Cancel")

    val passwordShort = Tx("पासवर्ड कम से कम 8 अक्षर का हो", "পাসওয়ার্ড কমপক্ষে ৮ অক্ষরের হতে হবে", "Password must be at least 8 characters")
    val passwordMismatch = Tx("दोनों पासवर्ड एक जैसे नहीं हैं", "দুটি পাসওয়ার্ড এক নয়", "The two passwords do not match")
    val noBiometric = Tx(
        "इस फ़ोन में फिंगरप्रिंट सेट नहीं है। फ़ोन की Settings में फिंगरप्रिंट जोड़कर वापस आएँ।",
        "এই ফোনে ফিঙ্গারপ্রিন্ট সেট করা নেই। ফোনের Settings-এ ফিঙ্গারপ্রিন্ট যোগ করে ফিরে আসুন।",
        "No fingerprint is set on this phone. Add one in the phone Settings and come back."
    )
    val codeInvalid = Tx("कोड ठीक 25 अंकों का होना चाहिए", "কোড ঠিক ২৫ সংখ্যার হতে হবে", "The code must be exactly 25 digits")
    val errHttps = Tx(
        "सर्वर का पता सही नहीं है। उदाहरण: nexora-backend-fq54.onrender.com",
        "সার্ভারের ঠিকানা সঠিক নয়। উদাহরণ: nexora-backend-fq54.onrender.com",
        "The server address is not valid. Example: nexora-backend-fq54.onrender.com"
    )
    val errNetwork = Tx(
        "सर्वर तक नहीं पहुँच सके। इंटरनेट देखें और थोड़ी देर बाद फिर कोशिश करें।",
        "সার্ভারে পৌঁছানো যায়নি। ইন্টারনেট দেখুন এবং একটু পরে আবার চেষ্টা করুন।",
        "Could not reach the server. Check the internet and try again shortly."
    )
    val errToken = Tx("Admin Token गलत है", "Admin Token ভুল", "The Admin Token is wrong")
    val errLocked = Tx(
        "बहुत ज़्यादा गलत कोशिशें हुईं, कुछ देर के लिए रोक लग गई है।",
        "অনেকবার ভুল চেষ্টা হয়েছে, কিছুক্ষণের জন্য আটকানো হয়েছে।",
        "Too many wrong attempts; it is blocked for a while."
    )
    val errServer = Tx("सर्वर से गड़बड़ मिली, फिर कोशिश करें।", "সার্ভার থেকে সমস্যা এসেছে, আবার চেষ্টা করুন।", "The server returned an error. Try again.")
    val errKeyInvalid = Tx(
        "इस फ़ोन की सुरक्षा-की बदल गई है (फिंगरप्रिंट बदला होगा)। 25 अंकों के कोड से नया जोड़ें।",
        "এই ফোনের সুরক্ষা-কী বদলে গেছে (ফিঙ্গারপ্রিন্ট বদলেছে)। ২৫ সংখ্যার কোড দিয়ে নতুন করে যোগ করুন।",
        "This phone's security key changed (the fingerprint was probably changed). Add it again with the 25-digit code."
    )
    val errWrongCode = Tx("25 अंकों का कोड गलत है", "২৫ সংখ্যার কোড ভুল", "The 25-digit code is wrong")
    val errAlready = Tx(
        "यह सर्वर पहले से किसी फ़ोन से जुड़ा है। 25 अंकों के कोड से जोड़ें।",
        "এই সার্ভার আগে থেকেই একটি ফোনের সাথে যুক্ত। ২৫ সংখ্যার কোড দিয়ে যুক্ত করুন।",
        "This server is already bound to a phone. Use the 25-digit code."
    )
    val errNotRegistered = Tx("इस सर्वर पर अभी कोई फ़ोन जुड़ा नहीं है।", "এই সার্ভারে এখনো কোনো ফোন যুক্ত নেই।", "No phone is bound to this server yet.")
    val errSignature = Tx(
        "सुरक्षा जाँच विफल। फ़ोन का समय/तारीख सही (ऑटोमेटिक) रखें और फिर कोशिश करें।",
        "নিরাপত্তা যাচাই ব্যর্থ। ফোনের সময়/তারিখ ঠিক (অটোমেটিক) রাখুন এবং আবার চেষ্টা করুন।",
        "Security check failed. Keep the phone date and time correct (automatic) and try again."
    )
    val errAuth = Tx("फिंगरप्रिंट जाँच पूरी नहीं हुई।", "ফিঙ্গারপ্রিন্ট যাচাই সম্পূর্ণ হয়নি।", "Fingerprint check was not completed.")
    val errUnknown = Tx("कुछ गड़बड़ हो गई, फिर कोशिश करें।", "কিছু একটা সমস্যা হয়েছে, আবার চেষ্টা করুন।", "Something went wrong. Try again.")
}
