package com.zentora.nexmora.admin

enum class Danger { BAN, UNBAN, DELETE, RESTORE }

object C {
    val tabControl = Tx("नियंत्रण", "নিয়ন্ত্রণ", "Control")
    val empty = Tx("यहाँ अभी कुछ नहीं है।", "এখানে এখনো কিছু নেই।", "Nothing here yet.")

    val menuArchive = Tx("डिलीट हुए खाते", "ডিলিট হওয়া অ্যাকাউন্ট", "Deleted accounts")
    val menuArchiveDesc = Tx("वापस लाने के लिए", "ফিরিয়ে আনতে", "Restore them from here")
    val menuBans = Tx("बैन किए यूज़र", "ব্যান করা ইউজার", "Banned users")
    val menuBansDesc = Tx("अनबैन करने के लिए", "আনব্যান করতে", "Unban them from here")
    val menuActions = Tx("आपके किए काम", "আপনার করা কাজ", "Your actions")
    val menuActionsDesc = Tx("बैन, डिलीट, रिस्टोर, निजी डेटा देखना", "ব্যান, ডিলিট, রিস্টোর, ব্যক্তিগত ডেটা দেখা", "Bans, deletes, restores, private views")
    val menuLedger = Tx("लेजर (सुरक्षा श्रृंखला)", "লেজার (নিরাপত্তা শৃঙ্খল)", "Ledger (security chain)")
    val menuLedgerDesc = Tx("हर बदलाव का अटूट रिकॉर्ड", "প্রতিটি পরিবর্তনের অটুট রেকর্ড", "Unbroken record of every change")
    val menuVaults = Tx("37 वॉल्ट", "৩৭ ভল্ট", "37 vaults")
    val menuVaultsDesc = Tx("हर वॉल्ट की हालत", "প্রতিটি ভল্টের অবস্থা", "Health of every vault")

    val secManage = Tx("नियंत्रण", "নিয়ন্ত্রণ", "Control")
    val banBtn = Tx("यूज़र को बैन करें", "ইউজারকে ব্যান করুন", "Ban this user")
    val unbanBtn = Tx("अनबैन करें", "আনব্যান করুন", "Unban this user")
    val deleteBtn = Tx("अकाउंट डिलीट करें", "অ্যাকাউন্ট ডিলিট করুন", "Delete this account")
    val restoreBtn = Tx("वापस लाएँ", "ফিরিয়ে আনুন", "Restore")

    val reasonLabel = Tx("कारण (ज़रूरी नहीं)", "কারণ (বাধ্যতামূলক নয়)", "Reason (optional)")
    val step3 = Tx(
        "आख़िरी कदम: अपना डिलीट पासवर्ड डालें।",
        "শেষ ধাপ: আপনার ডিলিট পাসওয়ার্ড দিন।",
        "Last step: enter your delete password."
    )
    val yesContinue = Tx("हाँ, आगे बढ़ें", "হ্যাঁ, এগিয়ে যান", "Yes, continue")
    val confirm = Tx("पक्का करें", "নিশ্চিত করুন", "Confirm")

    val wrongPassword = Tx("डिलीट पासवर्ड गलत है", "ডিলিট পাসওয়ার্ড ভুল", "The delete password is wrong")
    val alreadyBanned = Tx("यह यूज़र पहले से बैन है", "এই ইউজার আগেই ব্যান", "This user is already banned")
    val notBanned = Tx("यह यूज़र बैन नहीं है", "এই ইউজার ব্যান নয়", "This user is not banned")
    val notFound = Tx("यह रिकॉर्ड अब नहीं मिला", "এই রেকর্ড আর পাওয়া যায়নি", "This record was not found")
    val tooLarge = Tx("इस खाते का डेटा आर्काइव के लिए बहुत बड़ा है", "এই অ্যাকাউন্টের ডেটা আর্কাইভের জন্য অনেক বড়", "This account is too large to archive")
    val alreadyRestored = Tx("यह पहले ही वापस आ चुका है", "এটি আগেই ফিরে এসেছে", "This was already restored")
    val exists = Tx("इस पहचान का खाता सर्वर पर पहले से मौजूद है", "এই পরিচয়ের অ্যাকাউন্ট সার্ভারে আগেই আছে", "An account with this identity already exists")

    val kindAdmin = Tx("आपने डिलीट किया", "আপনি ডিলিট করেছেন", "Deleted by you")
    val kindSelf = Tx("यूज़र ने खुद डिलीट किया", "ইউজার নিজে ডিলিট করেছে", "Deleted by the user")
    val deletedOn = Tx("डिलीट की तारीख़", "ডিলিটের তারিখ", "Deleted on")
    val autoPurge = Tx("हमेशा के लिए मिटेगा", "চিরতরে মুছে যাবে", "Permanently erased on")
    val restoredOn = Tx("वापस लाया गया", "ফিরিয়ে আনা হয়েছে", "Restored on")
    val bannedOn = Tx("बैन की तारीख़", "ব্যানের তারিখ", "Banned on")
    val reason = Tx("कारण", "কারণ", "Reason")

    val verifyBtn = Tx("श्रृंखला जाँचें", "শৃঙ্খল যাচাই করুন", "Verify chain")
    val verifying = Tx("जाँच चल रही है…", "যাচাই চলছে…", "Verifying…")
    val chainValid = Tx("✅ श्रृंखला सही है, किसी ने छेड़छाड़ नहीं की।", "✅ শৃঙ্খল ঠিক আছে, কেউ হাত দেয়নি।", "✅ The chain is intact; nothing was tampered with.")
    val chainBroken = Tx("⚠️ श्रृंखला टूटी हुई है! तुरंत जाँच करें।", "⚠️ শৃঙ্খল ভাঙা! এখনই পরীক্ষা করুন।", "⚠️ The chain is BROKEN! Investigate now.")

    val vaultRecords = Tx("रिकॉर्ड", "রেকর্ড", "records")
    val vaultOps = Tx("काम", "কাজ", "operations")
    val vaultErrors = Tx("गड़बड़", "ত্রুটি", "errors")
    val lastOp = Tx("आख़िरी काम", "শেষ কাজ", "last activity")

    fun dangerTitle(kind: Danger): Tx = when (kind) {
        Danger.BAN -> Tx("यूज़र को बैन करें", "ইউজারকে ব্যান করুন", "Ban user")
        Danger.UNBAN -> Tx("यूज़र को अनबैन करें", "ইউজারকে আনব্যান করুন", "Unban user")
        Danger.DELETE -> Tx("अकाउंट डिलीट करें", "অ্যাকাউন্ট ডিলিট করুন", "Delete account")
        Danger.RESTORE -> Tx("अकाउंट वापस लाएँ", "অ্যাকাউন্ট ফিরিয়ে আনুন", "Restore account")
    }

    fun step1(kind: Danger, name: String): Tx = when (kind) {
        Danger.BAN -> Tx("क्या आप $name को बैन करना चाहते हैं?", "আপনি কি $name-কে ব্যান করতে চান?", "Do you want to ban $name?")
        Danger.UNBAN -> Tx("क्या $name को अनबैन करना है?", "$name-কে কি আনব্যান করবেন?", "Unban $name?")
        Danger.DELETE -> Tx("क्या $name का अकाउंट डिलीट करना है?", "$name-এর অ্যাকাউন্ট কি ডিলিট করবেন?", "Delete the account of $name?")
        Danger.RESTORE -> Tx("क्या $name का अकाउंट वापस लाना है?", "$name-এর অ্যাকাউন্ট কি ফিরিয়ে আনবেন?", "Restore the account of $name?")
    }

    fun step2(kind: Danger): Tx = when (kind) {
        Danger.BAN -> Tx(
            "बैन होते ही यूज़र को \"आपका अकाउंट बैन है\" दिखेगा और उसके सार्वजनिक वीडियो छिप जाएँगे। आप बाद में अनबैन कर सकते हैं। पक्का?",
            "ব্যান হলেই ইউজার \"আপনার অ্যাকাউন্ট ব্যান\" দেখবে এবং তার পাবলিক ভিডিও লুকিয়ে যাবে। পরে আনব্যান করা যাবে। নিশ্চিত?",
            "The user will see \"your account is banned\" and their public videos will be hidden. You can unban later. Are you sure?"
        )
        Danger.UNBAN -> Tx(
            "यूज़र फिर से ऐप चला पाएगा और छिपाए गए वीडियो वापस दिखेंगे। पक्का?",
            "ইউজার আবার অ্যাপ চালাতে পারবে এবং লুকানো ভিডিও ফিরে আসবে। নিশ্চিত?",
            "The user can use the app again and hidden videos return. Are you sure?"
        )
        Danger.DELETE -> Tx(
            "अकाउंट और उसका सारा डेटा सर्वर से हटकर सुरक्षित आर्काइव में चला जाएगा। आप चाहें तो वहाँ से वापस ला सकते हैं। पक्का?",
            "অ্যাকাউন্ট ও তার সব ডেটা সার্ভার থেকে সরে নিরাপদ আর্কাইভে যাবে। চাইলে সেখান থেকে ফিরিয়ে আনতে পারবেন। নিশ্চিত?",
            "The account and all its data move from the server into the safe archive. You can restore it from there. Are you sure?"
        )
        Danger.RESTORE -> Tx(
            "अकाउंट और उसका डेटा पहले जैसा लौट आएगा। पक्का?",
            "অ্যাকাউন্ট ও তার ডেটা আগের মতো ফিরে আসবে। নিশ্চিত?",
            "The account and its data come back as before. Are you sure?"
        )
    }
}
