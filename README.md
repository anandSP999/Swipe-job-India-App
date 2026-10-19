# SwipeJobs India - Android Mobile App

Swipe-to-apply job portal app for blue-collar, service, delivery, and front-line jobs across India.

## 📱 Download APK Directly

You can download and install the Android app directly on your phone:

- **[⬇️ Click here to download SwipeJobs-India.apk](https://github.com/anandSP999/Swipe-job-India-App/releases/download/v1.0/SwipeJobsIndia.apk)**

### How to Install:
1. Download the `SwipeJobs-India.apk` file to your Android smartphone.
2. Tap the downloaded file in your Notification bar or File Manager.
3. If prompted with *"Install unknown apps"*, tap **Allow / Enable** in Settings.
4. Tap **Install** and open the app!

---

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>SwipeJobs India — Screenshots</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    background: #0f172a; color: #fff; padding: 20px 0;
  }
  h2 { text-align: center; margin-bottom: 18px; font-size: 20px; font-weight: 600; }

  /* -------- Horizontal Scroll Gallery -------- */
  .gallery {
    display: flex;
    gap: 16px;
    overflow-x: auto;
    overflow-y: hidden;
    padding: 16px 20px 22px;
    scroll-snap-type: x mandatory;
    scroll-behavior: smooth;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: thin;
    scrollbar-color: #475569 transparent;
  }
  .gallery::-webkit-scrollbar { height: 8px; }
  .gallery::-webkit-scrollbar-thumb { background: #475569; border-radius: 4px; }
  .gallery::-webkit-scrollbar-track { background: transparent; }

  .gallery img {
    flex: 0 0 auto;
    width: 250px;
    height: auto;
    border-radius: 14px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.55);
    scroll-snap-align: start;
    cursor: zoom-in;
    transition: transform .2s ease;
    display: block;
  }
  .gallery img:hover { transform: scale(1.04); }

  /* -------- Lightbox Popup -------- */
  .lightbox {
    position: fixed; inset: 0;
    background: rgba(0,0,0,0.94);
    display: none;
    align-items: center;
    justify-content: center;
    z-index: 9999;
  }
  .lightbox.active { display: flex; }
  .lightbox img {
    max-width: 88vw;
    max-height: 82vh;
    border-radius: 12px;
    box-shadow: 0 0 50px rgba(0,0,0,0.9);
  }

  .lb-btn {
    position: absolute;
    background: rgba(255,255,255,0.12);
    border: 1px solid rgba(255,255,255,0.25);
    color: #fff;
    width: 54px; height: 54px;
    border-radius: 50%;
    font-size: 26px;
    line-height: 1;
    cursor: pointer;
    display: flex; align-items: center; justify-content: center;
    transition: background .2s;
    user-select: none;
    z-index: 10000;
  }
  .lb-btn:hover { background: rgba(255,255,255,0.28); }
  .lb-prev { left: 20px; top: 50%; transform: translateY(-50%); }
  .lb-next { right: 20px; top: 50%; transform: translateY(-50%); }
  .lb-close { top: 20px; right: 20px; }

  .lb-counter {
    position: absolute; bottom: 24px; left: 50%;
    transform: translateX(-50%);
    background: rgba(255,255,255,0.12);
    padding: 6px 16px; border-radius: 20px;
    font-size: 14px; letter-spacing: .5px;
  }

  @media (max-width: 640px) {
    .gallery img { width: 200px; }
    .lb-btn { width: 46px; height: 46px; font-size: 22px; }
    .lb-prev { left: 10px; } .lb-next { right: 10px; }
  }
</style>
</head>
<body>

<h2>📸 SwipeJobs India — App Screenshots</h2>

<div class="gallery" id="gallery">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/refs/heads/main/Swipe%20job%20India%20-%20App/Screenshot_2026-10-19-20-01-23-395_com.aistudio.swipejobs.india.jpg" alt="Screenshot 1" onclick="openLightbox(0)">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/refs/heads/main/Swipe%20job%20India%20-%20App/Screenshot_2026-10-19-20-00-59-816_com.aistudio.swipejobs.india.jpg" alt="Screenshot 2" onclick="openLightbox(1)">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/24f05495e23e0145437d15e52ae62f0ba83c1b58/Swipe%20job%20India%20-%20App/Screenshot_2026-10-19-19-58-40-922_com.aistudio.swipejobs.india.jpg" alt="Screenshot 3" onclick="openLightbox(2)">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/24f05495e23e0145437d15e52ae62f0ba83c1b58/Swipe%20job%20India%20-%20App/Screenshot_2026-10-19-19-58-11-958_com.aistudio.swipejobs.india.jpg" alt="Screenshot 4" onclick="openLightbox(3)">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/24f05495e23e0145437d15e52ae62f0ba83c1b58/Swipe%20job%20India%20-%20App/Screenshot_2026-10-19-19-56-43-828_com.aistudio.swipejobs.india.jpg" alt="Screenshot 5" onclick="openLightbox(4)">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/24f05495e23e0145437d15e52ae62f0ba83c1b58/Swipe%20job%20India%20-%20App/Screenshot_2026-10-19-19-55-13-122_com.aistudio.swipejobs.india-edit.jpg" alt="Screenshot 6" onclick="openLightbox(5)">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/24f05495e23e0145437d15e52ae62f0ba83c1b58/Swipe%20job%20India%20-%20App/Screenshot_2026-10-19-19-54-48-862_com.aistudio.swipejobs.india-edit.jpg" alt="Screenshot 7" onclick="openLightbox(6)">
  <img src="https://raw.githubusercontent.com/anandSP999/Swipe-job-India-App/24f05495e23e0145437d15e52ae62f0ba83c1b58/Swipe%20job%20India%20-%20App/IMG_20261019_200009.jpg" alt="Screenshot 8" onclick="openLightbox(7)">
</div>

<!-- Lightbox Popup -->
<div class="lightbox" id="lightbox" onclick="closeOnBackdrop(event)">
  <button class="lb-btn lb-close" onclick="closeLightbox()" aria-label="Close">✕</button>
  <button class="lb-btn lb-prev" onclick="showPrev()" aria-label="Previous">‹</button>
  <img id="lb-img" src="" alt="Full preview">
  <button class="lb-btn lb-next" onclick="showNext()" aria-label="Next">›</button>
  <div class="lb-counter" id="lb-counter"></div>
</div>

<script>
  const images = Array.from(document.querySelectorAll("#gallery img")).map(i => i.src);
  let index = 0;

  const lb    = document.getElementById("lightbox");
  const lbImg = document.getElementById("lb-img");
  const lbCnt = document.getElementById("lb-counter");

  function openLightbox(i) {
    index = i;
    update();
    lb.classList.add("active");
    document.body.style.overflow = "hidden";
  }
  function closeLightbox() {
    lb.classList.remove("active");
    document.body.style.overflow = "";
  }
  function closeOnBackdrop(e) { if (e.target === lb) closeLightbox(); }

  function showPrev() { index = (index - 1 + images.length) % images.length; update(); }
  function showNext() { index = (index + 1) % images.length; update(); }

  function update() {
    lbImg.src = images[index];
    lbCnt.textContent = (index + 1) + " / " + images.length;
  }

  // Keyboard navigation
  document.addEventListener("keydown", (e) => {
    if (!lb.classList.contains("active")) return;
    if (e.key === "ArrowLeft")  showPrev();
    if (e.key === "ArrowRight") showNext();
    if (e.key === "Escape")     closeLightbox();
  });
</script>
</body>
</html>

---

## ✨ Key Features
- **Swipe-to-Apply**: Swipe Right to Apply, Swipe Left to Pass.
- **Toggle View**: Switch between Card Swiping Deck and Dense List view anytime.
- **Undo Pass**: Easily undo an accidental pass.
- **Refer & Earn Wallet**: Share your referral code, track invites, and request payouts.
- **Multi-Account Manager**: Switch seamlessly between candidate profiles without logging out.
- **Digital Resume / CV Generator**: Formats candidate bio, skills, and work history.
- **Real-Time Job Alerts & Push Notifications**: Instant alerts for interview calls and status changes.
