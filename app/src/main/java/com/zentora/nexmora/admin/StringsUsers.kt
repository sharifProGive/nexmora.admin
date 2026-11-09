package com.zentora.nexmora.admin

object U {
    val tabUsers = Tx("यूज़र", "ইউজার", "Users")
    val searchHint = Tx("नाम, @हैंडल, ईमेल या ID", "নাম, @হ্যান্ডেল, ইমেল বা ID", "Name, @handle, email or ID")
    val searchBtn = Tx("खोजें", "খুঁজুন", "Search")
    val noUsers = Tx("कोई यूज़र नहीं मिला।", "কোনো ইউজার পাওয়া যায়নি।", "No users found.")
    val loadMore = Tx("और दिखाएँ", "আরও দেখান", "Load more")
    val bannedTag = Tx("बैन", "ব্যান", "BANNED")
    val statusActive = Tx("चालू", "সক্রিয়", "Active")
    val statusBanned = Tx("बैन", "ব্যান", "Banned")
    val joined = Tx("जुड़ा", "যোগ দিয়েছে", "Joined")
    val lastSeen = Tx("आख़िरी बार दिखा", "সর্বশেষ দেখা গেছে", "Last seen")
    val emailLabel = Tx("ईमेल", "ইমেল", "Email")
    val idLabel = Tx("पहचान (ID)", "পরিচয় (ID)", "ID")
    val statusLabel = Tx("हालत", "অবস্থা", "Status")
    val bannedSince = Tx("बैन की तारीख़", "ব্যানের তারিখ", "Banned since")
    val reason = Tx("कारण", "কারণ", "Reason")

    val secAccount = Tx("खाता", "অ্যাকাউন্ট", "Account")
    val secStats = Tx("गिनती", "পরিসংখ্যান", "Statistics")
    val secPrivate = Tx("निजी डेटा", "ব্যক্তিগত ডেটা", "Private data")
    val secVideos = Tx("वीडियो (नए 20)", "ভিডিও (নতুন ২০)", "Videos (latest 20)")
    val secComments = Tx("कमेंट (नए 20)", "কমেন্ট (নতুন ২০)", "Comments (latest 20)")
    val secFriends = Tx("दोस्त", "বন্ধু", "Friends")
    val secCalls = Tx("कॉल (नई 20)", "কল (নতুন ২০)", "Calls (latest 20)")

    val privateWarn = Tx(
        "फ़ोन नंबर और बिलिंग जानकारी छिपी रहती है। देखने पर सर्वर के लेजर में दर्ज हो जाता है कि आपने इसे देखा।",
        "ফোন নম্বর ও বিলিং তথ্য লুকানো থাকে। দেখলে সার্ভারের লেজারে লেখা থাকে যে আপনি দেখেছেন।",
        "Phone number and billing info stay hidden. Viewing them is recorded in the server ledger."
    )
    val showPrivate = Tx("निजी डेटा दिखाएँ", "ব্যক্তিগত ডেটা দেখান", "Show private data")
    val hide = Tx("छिपाएँ", "লুকান", "Hide")
    val phone = Tx("फ़ोन नंबर", "ফোন নম্বর", "Phone number")
    val billing = Tx("बिलिंग जानकारी", "বিলিং তথ্য", "Billing info")
    val confirmTitle = Tx("निजी डेटा देखें?", "ব্যক্তিগত ডেটা দেখবেন?", "View private data?")
    val confirmText = Tx(
        "यह देखना सर्वर के लेजर में दर्ज होगा।",
        "এটি দেখা সার্ভারের লেজারে লেখা হবে।",
        "This view will be recorded in the server ledger."
    )
    val yes = Tx("हाँ, दिखाओ", "হ্যাঁ, দেখান", "Yes, show")

    val stVideos = Tx("वीडियो", "ভিডিও", "Videos")
    val stViews = Tx("व्यू", "ভিউ", "Views")
    val stLikesRecv = Tx("मिले लाइक", "পাওয়া লাইক", "Likes received")
    val stComments = Tx("लिखे कमेंट", "লেখা কমেন্ট", "Comments written")
    val stLikesGiven = Tx("दिए लाइक", "দেওয়া লাইক", "Likes given")
    val stDislikesGiven = Tx("दिए डिसलाइक", "দেওয়া ডিসলাইক", "Dislikes given")
    val stFriends = Tx("दोस्त", "বন্ধু", "Friends")
    val stReqRecv = Tx("मिली रिक्वेस्ट", "পাওয়া রিকোয়েস্ট", "Requests received")
    val stReqSent = Tx("भेजी रिक्वेस्ट", "পাঠানো রিকোয়েস্ট", "Requests sent")
    val stChatSent = Tx("भेजे चैट संदेश", "পাঠানো চ্যাট বার্তা", "Chat messages sent")
    val stChatRecv = Tx("मिले चैट संदेश", "পাওয়া চ্যাট বার্তা", "Chat messages received")
    val stCallsMade = Tx("की गई कॉल", "করা কল", "Calls made")
    val stCallsRecv = Tx("आई कॉल", "আসা কল", "Calls received")
    val stMediaFiles = Tx("मीडिया फ़ाइलें", "মিডিয়া ফাইল", "Media files")
    val stMediaBytes = Tx("मीडिया स्टोरेज", "মিডিয়া স্টোরেজ", "Media storage")

    val views = Tx("व्यू", "ভিউ", "views")
    val likes = Tx("लाइक", "লাইক", "likes")
    val callOut = Tx("गई", "গেছে", "outgoing")
    val callIn = Tx("आई", "এসেছে", "incoming")
    val videoCall = Tx("वीडियो कॉल", "ভিডিও কল", "video call")
    val voiceCall = Tx("आवाज़ कॉल", "ভয়েস কল", "voice call")
    val withUser = Tx("साथ", "সাথে", "with")
}
