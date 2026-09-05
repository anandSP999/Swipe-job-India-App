const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = 3000;
const APK_PATH = path.join(__dirname, 'release', 'SwipeJobs-India.apk');

// In-memory catalog matching the Android app database
const JOBS = [
  {
    id: "sj_job_1",
    title: "Android App Developer (Jetpack Compose)",
    companyName: "Infosys Mobility Labs",
    companyEmail: "careers@infosys.com",
    location: "Bangalore, Karnataka",
    salary: "₹6.5 - ₹10.5 LPA",
    vacancy: "4",
    role: "Software Engineer",
    gender: "Both",
    edu: "B.Tech / BCA / MCA",
    working: "Hybrid (3 days office)",
    hiringFor: "Direct Company Payroll",
    benefits: "Health Insurance + Free Canteen + Performance Bonus",
    teamsize: "250+",
    interviewer: "Lead Mobile Architect",
    desc: "We are seeking a talented Android Developer experienced in Kotlin, Jetpack Compose, Coroutines, and MVVM architecture. You will craft next-generation consumer apps with seamless animations and real-time syncing.",
    category: "Computer",
    jobType: "hybrid",
    isCampaignActive: true,
    referralReward: 1200,
    applyCount: 48,
    status: "Approved",
    whatsapp: "919876543210"
  },
  {
    id: "sj_job_2",
    title: "Senior Digital Marketing Strategist",
    companyName: "Zomato Media Pvt Ltd",
    companyEmail: "talent@zomato.com",
    location: "Gurugram, Delhi NCR",
    salary: "₹5.0 - ₹8.0 LPA",
    vacancy: "2",
    role: "Marketing Manager",
    gender: "Both",
    edu: "Any Graduate / MBA",
    working: "Monday - Friday (Full Time)",
    hiringFor: "Direct Payroll",
    benefits: "Meal Credits + Gym Subsidy + ESOPs",
    teamsize: "500+",
    interviewer: "Head of Growth & Brand",
    desc: "Scale our social viral marketing campaigns, influencer partnerships, and user acquisition pipelines. Strong copywriting, data analytics, and ROAS optimization skills required.",
    category: "Marketing",
    jobType: "full-time",
    isCampaignActive: true,
    referralReward: 800,
    applyCount: 62,
    status: "Approved",
    whatsapp: "919876543211"
  },
  {
    id: "sj_job_3",
    title: "Customer Support Specialist (Voice/Chat)",
    companyName: "Swiggy India Operations",
    companyEmail: "jobs@swiggy.in",
    location: "Pune, Maharashtra",
    salary: "₹22,000 - ₹32,000 / mo",
    vacancy: "15",
    role: "Support Associate",
    gender: "Both",
    edu: "12th Pass / Any Graduate",
    working: "Rotational Shifts (5 Days/wk)",
    hiringFor: "Direct Operations",
    benefits: "Night Shift Allowance + Cab Facility + Insurance",
    teamsize: "1000+",
    interviewer: "Operations Manager",
    desc: "Assist valued restaurant partners and customers with high empathy, quick issue resolution, and friendly communication in English and Hindi.",
    category: "Telecaller",
    jobType: "full-time",
    isCampaignActive: true,
    referralReward: 600,
    applyCount: 95,
    status: "Approved",
    whatsapp: "919876543212"
  },
  {
    id: "sj_job_4",
    title: "Financial Accountant & GST Specialist",
    companyName: "Tata Consultancy Services",
    companyEmail: "finance-hiring@tcs.com",
    location: "Mumbai, Maharashtra",
    salary: "₹4.5 - ₹7.2 LPA",
    vacancy: "3",
    role: "Finance Analyst",
    gender: "Both",
    edu: "B.Com / M.Com / Inter CA",
    working: "Standard Corporate (9:30 AM - 6 PM)",
    hiringFor: "Direct Tata Enterprise",
    benefits: "Gratuity + Yearly Bonus + Medical Cover",
    teamsize: "10,000+",
    interviewer: "Senior Finance Director",
    desc: "Responsible for corporate ledger reconciliation, GST return filings, TDS compliance, and preparation of monthly P&L accounts.",
    category: "Accounts",
    jobType: "full-time",
    isCampaignActive: true,
    referralReward: 1000,
    applyCount: 37,
    status: "Approved",
    whatsapp: "919876543213"
  },
  {
    id: "sj_job_5",
    title: "Territory Field Sales Executive",
    companyName: "Reliance Retail JioMart",
    companyEmail: "recruitment@reliance.com",
    location: "Ahmedabad, Gujarat",
    salary: "₹28,000 - ₹40,000 + Daily TA/DA",
    vacancy: "8",
    role: "Sales Executive",
    gender: "Male",
    edu: "10th / 12th Pass / Graduate",
    working: "Field Visits (10 AM - 7 PM)",
    hiringFor: "Direct Reliance Payroll",
    benefits: "Mobile Allowance + Travel Fuel + High Commission",
    teamsize: "2000+",
    interviewer: "Area Sales Manager",
    desc: "Onboard local retail kirana stores onto JioMart B2B platform. Promote FMCG catalogue discounts and achieve weekly order targets.",
    category: "Field Marketing",
    jobType: "full-time",
    isCampaignActive: true,
    referralReward: 750,
    applyCount: 81,
    status: "Approved",
    whatsapp: "919876543214"
  },
  {
    id: "sj_job_6",
    title: "HR Talent Acquisition Executive",
    companyName: "Paytm Financial Services",
    companyEmail: "hr@paytm.com",
    location: "Noida, Uttar Pradesh",
    salary: "₹3.8 - ₹6.0 LPA",
    vacancy: "2",
    role: "HR Specialist",
    gender: "Female",
    edu: "MBA in HR / Any Graduate",
    working: "Monday - Friday (Hybrid)",
    hiringFor: "Direct Payroll",
    benefits: "Laptop Provided + Wellness Benefits + Paid Leaves",
    teamsize: "1200+",
    interviewer: "HR Business Partner",
    desc: "Handle full-cycle recruitment for tech and sales roles. Screen candidate resumes, coordinate interview rounds, and prepare offer rollouts.",
    category: "HR",
    jobType: "hybrid",
    isCampaignActive: true,
    referralReward: 900,
    applyCount: 44,
    status: "Approved",
    whatsapp: "919876543215"
  }
];

function getHtmlPage(initialJobId = null, referralCode = null) {
  const targetJob = initialJobId ? JOBS.find(j => j.id === initialJobId) || JOBS[0] : null;
  const pageTitle = targetJob 
    ? `${targetJob.title} at ${targetJob.companyName} | SwipeJobs India`
    : "SwipeJobs India - Verified Direct Jobs & 1-Tap Apply";
  const pageDesc = targetJob
    ? `💰 ${targetJob.salary} • 📍 ${targetJob.location} • Direct Payroll • Apply on SwipeJobs India App or Web`
    : "India's fastest job platform with swipe matching, verified employer payrolls, instant HR WhatsApp connect, and AI Resume tools.";
  const canonicalUrl = targetJob 
    ? `https://ais-pre-jbmiw3g2ezswn7gcdlmgyn-637005264324.asia-southeast1.run.app/job/${targetJob.id}${referralCode ? '?ref=' + referralCode : ''}`
    : "https://ais-pre-jbmiw3g2ezswn7gcdlmgyn-637005264324.asia-southeast1.run.app/";

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover" />
  <title>${pageTitle}</title>
  
  <!-- Open Graph / WhatsApp Preview Meta Tags -->
  <meta property="og:type" content="website" />
  <meta property="og:title" content="${pageTitle}" />
  <meta property="og:description" content="${pageDesc}" />
  <meta property="og:url" content="${canonicalUrl}" />
  <meta property="og:image" content="https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=1200&auto=format&fit=crop&q=80" />
  <meta name="twitter:card" content="summary_large_image" />
  <meta name="twitter:title" content="${pageTitle}" />
  <meta name="twitter:description" content="${pageDesc}" />
  
  <!-- iOS PWA meta tags -->
  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />
  <meta name="apple-mobile-web-app-title" content="SwipeJobs" />
  <link rel="manifest" href="/manifest.json" />
  <meta name="theme-color" content="#1E40AF" />

  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    tailwind.config = {
      theme: {
        extend: {
          colors: {
            brand: {
              50: '#eff6ff',
              500: '#2563eb',
              600: '#1d4ed8',
              700: '#1e40af',
              900: '#1e3a8a',
            },
            accent: {
              green: '#10b981',
              coral: '#f43f5e',
              amber: '#f59e0b',
            }
          },
          fontFamily: {
            sans: ['-apple-system', 'BlinkMacSystemFont', 'SF Pro Text', 'SF Pro Display', 'Inter', 'system-ui', 'sans-serif'],
          }
        }
      }
    }
  </script>

  <!-- React 18 & Babel Standalone -->
  <script crossorigin src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
  <script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>

  <style>
    body {
      -webkit-font-smoothing: antialiased;
      -webkit-tap-highlight-color: transparent;
      user-select: none;
      background-color: #f8fafc;
    }
    .ios-blur {
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
    }
    .card-shadow {
      box-shadow: 0 10px 30px -10px rgba(0,0,0,0.12), 0 4px 6px -4px rgba(0,0,0,0.05);
    }
    .safe-bottom {
      padding-bottom: max(16px, env(safe-area-inset-bottom));
    }
    @keyframes pulse-subtle {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.6; }
    }
    .animate-shimmer {
      animation: pulse-subtle 1.5s infinite ease-in-out;
    }
  </style>

  ${initialJobId ? `
  <script>
    // Automatic deep link attempt for Android users with SwipeJobs installed
    (function() {
      var isAndroid = /Android/i.test(navigator.userAgent);
      if (isAndroid) {
        var deepLink = "swipejobs://job/${initialJobId}";
        var clickedAt = +new Date();
        window.location.href = deepLink;
      }
    })();
  </script>
  ` : ''}
</head>
<body class="text-slate-900 overflow-x-hidden min-h-screen">
  <div id="root"></div>

  <script type="text/babel">
    const { useState, useEffect, useMemo, useRef } = React;

    const ALL_JOBS = ${JSON.stringify(JOBS)};
    const INITIAL_JOB_ID = ${initialJobId ? `"${initialJobId}"` : "null"};
    const REFERRAL_CODE = ${referralCode ? `"${referralCode}"` : '"SWIPE2026"'};

    function App() {
      const [jobs, setJobs] = useState([]);
      const [displayedCount, setDisplayedCount] = useState(4);
      const [isLoadingChunk, setIsLoadingChunk] = useState(false);
      const [activeTab, setActiveTab] = useState('jobs'); // 'jobs' | 'ai-resume' | 'profile'
      const [selectedCategory, setSelectedCategory] = useState('All');
      const [searchQuery, setSearchQuery] = useState('');
      const [activeModalJob, setActiveModalJob] = useState(null);
      const [applyModalJob, setApplyModalJob] = useState(null);
      const [appliedJobs, setAppliedJobs] = useState([]);
      const [candidateName, setCandidateName] = useState('Rahul Sharma');
      const [candidateMobile, setCandidateMobile] = useState('9876543210');
      const [showSuccessToast, setShowSuccessToast] = useState(null);
      const [showIosPrompt, setShowIosPrompt] = useState(false);

      // Check iOS environment
      useEffect(() => {
        const isIos = /iPhone|iPad|iPod/i.test(navigator.userAgent);
        const isStandalone = window.navigator.standalone === true || window.matchMedia('(display-mode: standalone)').matches;
        if (isIos && !isStandalone) {
          setShowIosPrompt(true);
        }
      }, []);

      // Progressive micro-chunk loading of jobs
      useEffect(() => {
        // Instant first paint with first 3 jobs
        setJobs(ALL_JOBS);
        if (INITIAL_JOB_ID) {
          const match = ALL_JOBS.find(j => j.id === INITIAL_JOB_ID);
          if (match) setActiveModalJob(match);
        }
      }, []);

      // Filtered jobs
      const filteredJobs = useMemo(() => {
        return jobs.filter(j => {
          const matchCat = selectedCategory === 'All' || j.category === selectedCategory;
          const q = searchQuery.toLowerCase();
          const matchSearch = !q || j.title.toLowerCase().includes(q) || j.companyName.toLowerCase().includes(q) || j.location.toLowerCase().includes(q);
          return matchCat && matchSearch;
        });
      }, [jobs, selectedCategory, searchQuery]);

      // Visible chunk
      const visibleJobs = useMemo(() => {
        return filteredJobs.slice(0, displayedCount);
      }, [filteredJobs, displayedCount]);

      const loadMore = () => {
        if (displayedCount >= filteredJobs.size || isLoadingChunk) return;
        setIsLoadingChunk(true);
        setTimeout(() => {
          setDisplayedCount(prev => Math.min(prev + 3, filteredJobs.length));
          setIsLoadingChunk(false);
        }, 300);
      };

      const handleApply = (job) => {
        setApplyModalJob(job);
      };

      const submitApplication = (e) => {
        e.preventDefault();
        if (!applyModalJob) return;
        setAppliedJobs(prev => [...prev, { ...applyModalJob, appliedAt: new Date().toLocaleDateString() }]);
        setShowSuccessToast("Application submitted successfully to " + applyModalJob.companyName + "!");
        setApplyModalJob(null);
        setTimeout(() => setShowSuccessToast(null), 4000);
      };

      const categories = ["All", "Computer", "Marketing", "Telecaller", "Field Marketing", "Accounts", "HR"];

      return (
        <div class="max-w-md mx-auto min-h-screen bg-slate-50 flex flex-col shadow-2xl relative">
          
          {/* Smart App Banner */}
          <header class="sticky top-0 z-40 bg-white/90 ios-blur border-b border-slate-200/80 px-4 py-3">
            <div class="flex items-center justify-between gap-3">
              <div class="flex items-center gap-2.5">
                <div class="w-10 h-10 rounded-2xl bg-gradient-to-tr from-blue-700 to-indigo-600 flex items-center justify-center text-white font-black text-xl shadow-md">
                  S
                </div>
                <div>
                  <h1 class="text-base font-bold tracking-tight leading-tight text-slate-900 flex items-center gap-1.5">
                    SwipeJobs India
                    <span class="bg-blue-100 text-blue-700 text-[10px] font-extrabold px-1.5 py-0.5 rounded-full uppercase">App / Web</span>
                  </h1>
                  <p class="text-[11px] text-slate-500 font-medium">Direct Payroll • 0 Brokerage</p>
                </div>
              </div>

              {/* Direct APK Download Button */}
              <a
                href="/download"
                class="bg-blue-600 hover:bg-blue-700 active:scale-95 transition-all text-white text-xs font-semibold px-3 py-2 rounded-xl flex items-center gap-1.5 shadow-sm"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/>
                </svg>
                <span>Download APK</span>
              </a>
            </div>

            {/* iOS Smart Banner hint if Safari */}
            {showIosPrompt && (
              <div class="mt-2.5 bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200/70 rounded-xl p-2.5 flex items-center justify-between text-xs">
                <div class="flex items-center gap-2 text-blue-900">
                  <span class="text-base">📲</span>
                  <span>iPhone User? Tap <b>Share</b> &rarr; <b>Add to Home Screen</b> for 120Hz smooth app!</span>
                </div>
                <button onClick={() => setShowIosPrompt(false)} class="text-slate-400 hover:text-slate-600 font-bold px-1.5">✕</button>
              </div>
            )}
          </header>

          {/* Tab Navigation */}
          <div class="px-4 pt-3 pb-1 bg-white border-b border-slate-100 flex gap-2">
            <button
              onClick={() => setActiveTab('jobs')}
              class={'flex-1 py-2 rounded-xl text-xs font-bold transition-all ' + (activeTab === 'jobs' ? 'bg-blue-600 text-white shadow-sm' : 'bg-slate-100 text-slate-600 hover:bg-slate-200')}
            >
              🔥 Verified Jobs ({filteredJobs.length})
            </button>
            <button
              onClick={() => setActiveTab('ai-resume')}
              class={'flex-1 py-2 rounded-xl text-xs font-bold transition-all ' + (activeTab === 'ai-resume' ? 'bg-blue-600 text-white shadow-sm' : 'bg-slate-100 text-slate-600 hover:bg-slate-200')}
            >
              ✨ AI ATS Resume
            </button>
            <button
              onClick={() => setActiveTab('applied')}
              class={'flex-1 py-2 rounded-xl text-xs font-bold transition-all ' + (activeTab === 'applied' ? 'bg-blue-600 text-white shadow-sm' : 'bg-slate-100 text-slate-600 hover:bg-slate-200')}
            >
              📋 Applied ({appliedJobs.length})
            </button>
          </div>

          {/* Main Content Area */}
          <main class="flex-1 p-4 pb-24">
            
            {activeTab === 'jobs' && (
              <div class="space-y-4">
                {/* Search Bar */}
                <div class="relative">
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => { setSearchQuery(e.target.value); setDisplayedCount(4); }}
                    placeholder="Search by role, company, or city..."
                    class="w-full pl-10 pr-4 py-2.5 rounded-2xl bg-white border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 shadow-sm"
                  />
                  <svg class="w-5 h-5 text-slate-400 absolute left-3.5 top-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                  </svg>
                </div>

                {/* Category Pills */}
                <div class="flex gap-2 overflow-x-auto pb-1.5 scrollbar-none">
                  {categories.map(cat => (
                    <button
                      key={cat}
                      onClick={() => { setSelectedCategory(cat); setDisplayedCount(4); }}
                      class={'px-3 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all ' + (selectedCategory === cat ? 'bg-blue-600 text-white shadow' : 'bg-white border border-slate-200 text-slate-700')}
                    >
                      {cat}
                    </button>
                  ))}
                </div>

                {/* Progressive Job Cards List */}
                <div class="space-y-3.5">
                  {visibleJobs.map(job => (
                    <div
                      key={job.id}
                      onClick={() => setActiveModalJob(job)}
                      class="bg-white rounded-3xl p-4 border border-slate-200/80 card-shadow hover:border-blue-300 transition-all cursor-pointer active:scale-[0.99] relative overflow-hidden"
                    >
                      {/* Reward / Referral Tag */}
                      <div class="flex items-start justify-between gap-2 mb-2">
                        <span class="inline-flex items-center gap-1 bg-emerald-50 text-emerald-700 text-[11px] font-bold px-2.5 py-1 rounded-full border border-emerald-200/60">
                          <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-ping"></span>
                          Direct Payroll
                        </span>
                        <span class="text-xs font-extrabold text-blue-700 bg-blue-50 px-2.5 py-1 rounded-xl">
                          {job.salary}
                        </span>
                      </div>

                      <h3 class="text-base font-bold text-slate-900 leading-snug">{job.title}</h3>
                      <p class="text-xs font-semibold text-slate-600 mt-0.5">{job.companyName}</p>

                      <div class="flex flex-wrap items-center gap-3 mt-3 text-xs text-slate-500 font-medium">
                        <span class="flex items-center gap-1">
                          📍 {job.location}
                        </span>
                        <span class="flex items-center gap-1">
                          💼 {job.working}
                        </span>
                        <span class="flex items-center gap-1 text-slate-400">
                          👥 {job.vacancy} Openings
                        </span>
                      </div>

                      {/* Card Action Buttons */}
                      <div class="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                        <button
                          onClick={(e) => { e.stopPropagation(); setActiveModalJob(job); }}
                          class="text-xs font-bold text-blue-600 hover:text-blue-700 flex items-center gap-1"
                        >
                          View Full Details &rarr;
                        </button>
                        <div class="flex items-center gap-2">
                          <a
                            href={'https://api.whatsapp.com/send?phone=' + (job.whatsapp || '919876543210') + '&text=' + encodeURIComponent('Hi HR, I am applying for ' + job.title + ' at ' + job.companyName + ' (via SwipeJobs India)')}
                            target="_blank"
                            onClick={(e) => e.stopPropagation()}
                            class="bg-emerald-500 hover:bg-emerald-600 text-white p-2 rounded-xl text-xs font-bold flex items-center gap-1"
                            title="Chat with HR on WhatsApp"
                          >
                            <span>WhatsApp</span>
                          </a>
                          <button
                            onClick={(e) => { e.stopPropagation(); handleApply(job); }}
                            class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-xl text-xs font-bold active:scale-95 shadow-sm"
                          >
                            Apply Now
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}

                  {/* Empty state */}
                  {visibleJobs.length === 0 && (
                    <div class="bg-white rounded-3xl p-8 text-center border border-slate-200">
                      <p class="text-3xl">🔍</p>
                      <h4 class="text-sm font-bold text-slate-800 mt-2">No jobs matched your filter</h4>
                      <p class="text-xs text-slate-500 mt-1">Try searching for other categories or resetting filters.</p>
                      <button
                        onClick={() => { setSelectedCategory('All'); setSearchQuery(''); }}
                        class="mt-3 bg-blue-600 text-white text-xs font-bold px-4 py-2 rounded-xl"
                      >
                        Reset All Filters
                      </button>
                    </div>
                  )}

                  {/* Progressive Load More Micro-Chunk */}
                  {visibleJobs.length < filteredJobs.length && (
                    <div class="text-center pt-2">
                      <button
                        onClick={loadMore}
                        disabled={isLoadingChunk}
                        class="w-full bg-white hover:bg-slate-100 active:scale-98 border border-slate-300 text-slate-800 text-xs font-bold py-3 rounded-2xl shadow-sm flex items-center justify-center gap-2 transition-all"
                      >
                        {isLoadingChunk ? (
                          <>
                            <div class="w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                            <span>Loading next openings...</span>
                          </>
                        ) : (
                          <>
                            <span>Show More Jobs ({filteredJobs.length - visibleJobs.length} remaining)</span>
                            <span>&darr;</span>
                          </>
                        )}
                      </button>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* AI Resume ATS Builder Tab */}
            {activeTab === 'ai-resume' && (
              <div class="space-y-4">
                <div class="bg-gradient-to-tr from-blue-700 to-indigo-700 text-white rounded-3xl p-5 shadow-lg">
                  <div class="flex items-center gap-2">
                    <span class="text-xl">✨</span>
                    <h2 class="text-base font-bold">1-Click AI ATS Resume</h2>
                  </div>
                  <p class="text-xs text-blue-100 mt-1">
                    Generate an instant ATS-compliant professional resume pre-filled with your profile. Ready to download as PDF or share with HR.
                  </p>
                </div>

                <div class="bg-white rounded-3xl p-5 border border-slate-200 space-y-3">
                  <div>
                    <label class="text-xs font-bold text-slate-700">Full Name</label>
                    <input
                      type="text"
                      value={candidateName}
                      onChange={(e) => setCandidateName(e.target.value)}
                      class="w-full mt-1 p-2.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label class="text-xs font-bold text-slate-700">Mobile Number</label>
                    <input
                      type="text"
                      value={candidateMobile}
                      onChange={(e) => setCandidateMobile(e.target.value)}
                      class="w-full mt-1 p-2.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label class="text-xs font-bold text-slate-700">Highest Qualification</label>
                    <input
                      type="text"
                      defaultValue="Bachelor of Computer Applications (BCA)"
                      class="w-full mt-1 p-2.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label class="text-xs font-bold text-slate-700">Key Skills (Comma separated)</label>
                    <input
                      type="text"
                      defaultValue="Jetpack Compose, Kotlin, Android SDK, Git, REST APIs, Firebase"
                      class="w-full mt-1 p-2.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <button
                    onClick={() => {
                      window.print();
                    }}
                    class="w-full mt-3 bg-blue-600 hover:bg-blue-700 active:scale-95 text-white font-bold text-xs py-3 rounded-2xl shadow-sm flex items-center justify-center gap-2"
                  >
                    <span>📄 Download ATS Resume (PDF)</span>
                  </button>
                </div>
              </div>
            )}

            {/* Applied Jobs Tab */}
            {activeTab === 'applied' && (
              <div class="space-y-3">
                <h3 class="text-sm font-bold text-slate-800">Your Submitted Applications</h3>
                {appliedJobs.length === 0 ? (
                  <div class="bg-white rounded-3xl p-8 text-center border border-slate-200">
                    <p class="text-4xl">📮</p>
                    <h4 class="text-sm font-bold text-slate-800 mt-2">No applications yet</h4>
                    <p class="text-xs text-slate-500 mt-1">Browse verified vacancies and apply with 1 tap!</p>
                    <button
                      onClick={() => setActiveTab('jobs')}
                      class="mt-3 bg-blue-600 text-white text-xs font-bold px-4 py-2 rounded-xl"
                    >
                      Browse Jobs
                    </button>
                  </div>
                ) : (
                  appliedJobs.map((app, idx) => (
                    <div key={idx} class="bg-white rounded-3xl p-4 border border-slate-200 space-y-2 card-shadow">
                      <div class="flex items-center justify-between">
                        <span class="text-xs font-bold text-slate-900">{app.title}</span>
                        <span class="text-[10px] font-bold bg-amber-100 text-amber-800 px-2 py-0.5 rounded-full">In Review</span>
                      </div>
                      <p class="text-xs text-slate-600">{app.companyName} • {app.location}</p>
                      <div class="flex items-center justify-between text-[11px] text-slate-400 pt-2 border-t border-slate-100">
                        <span>Applied on: {app.appliedAt}</span>
                        <span class="text-emerald-600 font-semibold">Direct HR Verified</span>
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}

          </main>

          {/* Full Job Details Modal */}
          {activeModalJob && (
            <div class="fixed inset-0 z-50 bg-slate-900/60 ios-blur flex items-end sm:items-center justify-center p-0 sm:p-4 animate-fade-in">
              <div class="bg-white w-full max-w-md rounded-t-3xl sm:rounded-3xl max-h-[85vh] flex flex-col overflow-hidden shadow-2xl animate-slide-up">
                
                {/* Modal Header */}
                <div class="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
                  <div class="flex items-center gap-2">
                    <span class="bg-blue-600 text-white text-[10px] font-bold px-2 py-0.5 rounded-full">JOB DETAILS</span>
                    <span class="text-xs text-slate-500">ID: {activeModalJob.id}</span>
                  </div>
                  <button
                    onClick={() => setActiveModalJob(null)}
                    class="w-8 h-8 rounded-full bg-slate-200/80 hover:bg-slate-300 flex items-center justify-center text-slate-700 font-bold"
                  >
                    ✕
                  </button>
                </div>

                {/* Modal Scrollable Content */}
                <div class="p-5 overflow-y-auto space-y-4 text-xs">
                  <div>
                    <h2 class="text-lg font-extrabold text-slate-900 leading-tight">{activeModalJob.title}</h2>
                    <p class="text-sm font-semibold text-blue-600 mt-0.5">{activeModalJob.companyName}</p>
                  </div>

                  {/* Highlights Card */}
                  <div class="grid grid-cols-2 gap-2 bg-slate-50 p-3.5 rounded-2xl border border-slate-100">
                    <div>
                      <span class="text-[10px] text-slate-400 font-bold uppercase">Salary</span>
                      <p class="text-xs font-extrabold text-slate-900 mt-0.5">{activeModalJob.salary}</p>
                    </div>
                    <div>
                      <span class="text-[10px] text-slate-400 font-bold uppercase">Location</span>
                      <p class="text-xs font-bold text-slate-900 mt-0.5">{activeModalJob.location}</p>
                    </div>
                    <div class="mt-2">
                      <span class="text-[10px] text-slate-400 font-bold uppercase">Working Hours</span>
                      <p class="text-xs font-bold text-slate-900 mt-0.5">{activeModalJob.working}</p>
                    </div>
                    <div class="mt-2">
                      <span class="text-[10px] text-slate-400 font-bold uppercase">Vacancies</span>
                      <p class="text-xs font-bold text-slate-900 mt-0.5">{activeModalJob.vacancy} positions</p>
                    </div>
                  </div>

                  {/* Perks & Benefits */}
                  <div>
                    <h4 class="font-bold text-slate-800 uppercase tracking-wider text-[11px]">Benefits & Perks</h4>
                    <p class="mt-1 text-slate-600 bg-emerald-50 text-emerald-900 p-2.5 rounded-xl border border-emerald-200/60">
                      🎁 {activeModalJob.benefits}
                    </p>
                  </div>

                  {/* Job Description */}
                  <div>
                    <h4 class="font-bold text-slate-800 uppercase tracking-wider text-[11px]">Job Description & Requirements</h4>
                    <p class="mt-1 text-slate-600 leading-relaxed bg-slate-50 p-3 rounded-xl border border-slate-100">
                      {activeModalJob.desc}
                    </p>
                  </div>

                  {/* Verified Notice */}
                  <div class="bg-blue-50 border border-blue-200/60 rounded-2xl p-3 flex items-center gap-2.5">
                    <span class="text-xl">🛡️</span>
                    <div>
                      <h5 class="text-xs font-bold text-blue-900">Zero Charges Guaranteed</h5>
                      <p class="text-[11px] text-blue-700">SwipeJobs verified this employer. No candidate registration fee allowed.</p>
                    </div>
                  </div>
                </div>

                {/* Modal Footer Actions */}
                <div class="p-4 border-t border-slate-100 bg-white flex items-center gap-2.5">
                  <a
                    href={'https://api.whatsapp.com/send?phone=' + (activeModalJob.whatsapp || '919876543210') + '&text=' + encodeURIComponent('Hi HR, I am applying for ' + activeModalJob.title + ' at ' + activeModalJob.companyName + ' (via SwipeJobs India)')}
                    target="_blank"
                    class="flex-1 bg-emerald-500 hover:bg-emerald-600 text-white font-bold py-3 rounded-2xl flex items-center justify-center gap-1.5 shadow-sm active:scale-95 transition-all text-xs"
                  >
                    <span>💬 WhatsApp HR</span>
                  </a>
                  <button
                    onClick={() => {
                      const j = activeModalJob;
                      setActiveModalJob(null);
                      handleApply(j);
                    }}
                    class="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-2xl text-xs active:scale-95 transition-all shadow-md"
                  >
                    1-Tap Apply
                  </button>
                </div>

              </div>
            </div>
          )}

          {/* 1-Tap Apply Modal */}
          {applyModalJob && (
            <div class="fixed inset-0 z-50 bg-slate-900/60 ios-blur flex items-center justify-center p-4">
              <div class="bg-white w-full max-w-sm rounded-3xl p-5 shadow-2xl space-y-4">
                <div class="flex items-center justify-between">
                  <h3 class="text-sm font-bold text-slate-900">Confirm Application</h3>
                  <button onClick={() => setApplyModalJob(null)} class="text-slate-400 hover:text-slate-600 font-bold">✕</button>
                </div>
                <div class="bg-blue-50 p-3 rounded-2xl border border-blue-100">
                  <p class="text-xs font-bold text-blue-900">{applyModalJob.title}</p>
                  <p class="text-[11px] text-blue-700">{applyModalJob.companyName} • {applyModalJob.salary}</p>
                </div>
                <form onSubmit={submitApplication} class="space-y-3">
                  <div>
                    <label class="text-[11px] font-bold text-slate-600">Your Full Name</label>
                    <input
                      type="text"
                      required
                      value={candidateName}
                      onChange={(e) => setCandidateName(e.target.value)}
                      class="w-full mt-1 p-2 text-xs bg-slate-50 border border-slate-200 rounded-xl"
                    />
                  </div>
                  <div>
                    <label class="text-[11px] font-bold text-slate-600">Your WhatsApp Mobile</label>
                    <input
                      type="tel"
                      required
                      value={candidateMobile}
                      onChange={(e) => setCandidateMobile(e.target.value)}
                      class="w-full mt-1 p-2 text-xs bg-slate-50 border border-slate-200 rounded-xl"
                    />
                  </div>
                  <button
                    type="submit"
                    class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-2xl text-xs active:scale-95 shadow-md transition-all"
                  >
                    Submit Application
                  </button>
                </form>
              </div>
            </div>
          )}

          {/* Success Toast */}
          {showSuccessToast && (
            <div class="fixed top-5 left-1/2 -translate-x-1/2 z-50 bg-slate-900 text-white text-xs font-semibold px-4 py-2.5 rounded-full shadow-2xl flex items-center gap-2 border border-slate-700">
              <span class="text-emerald-400 text-base">✓</span>
              <span>{showSuccessToast}</span>
            </div>
          )}

        </div>
      );
    }

    ReactDOM.render(<App />, document.getElementById('root'));
  </script>
</body>
</html>`;
}

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  // CORS Headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  // APK Download endpoint
  if (pathname === '/download' || pathname === '/SwipeJobs-India.apk' || pathname === '/release/SwipeJobs-India.apk') {
    if (fs.existsSync(APK_PATH)) {
      const stat = fs.statSync(APK_PATH);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="SwipeJobs-India.apk"',
        'Cache-Control': 'no-cache'
      });
      const stream = fs.createReadStream(APK_PATH);
      stream.pipe(res);
      return;
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('APK not found. Please build the project first.');
      return;
    }
  }

  // PWA Manifest
  if (pathname === '/manifest.json') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      short_name: "SwipeJobs",
      name: "SwipeJobs India - Verified Direct Payroll",
      icons: [
        {
          src: "https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=192&h=192&fit=crop",
          type: "image/png",
          sizes: "192x192"
        },
        {
          src: "https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=512&h=512&fit=crop",
          type: "image/png",
          sizes: "512x512"
        }
      ],
      start_url: "/",
      background_color: "#1E40AF",
      theme_color: "#1E40AF",
      display: "standalone",
      orientation: "portrait"
    }));
    return;
  }

  // API: Get all jobs
  if (pathname === '/api/jobs') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, count: JOBS.length, jobs: JOBS }));
    return;
  }

  // API: Get single job
  if (pathname.startsWith('/api/job/')) {
    const jobId = pathname.replace('/api/job/', '');
    const job = JOBS.find(j => j.id === jobId);
    if (job) {
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, job }));
    } else {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: false, message: 'Job not found' }));
    }
    return;
  }

  // Deep Link Route: /job/:jobId
  if (pathname.startsWith('/job/')) {
    const segments = pathname.split('/').filter(Boolean);
    const jobId = segments[1];
    const referralCode = parsedUrl.query.ref || null;
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(getHtmlPage(jobId, referralCode));
    return;
  }

  // Fallback Root Route: /
  const referralCode = parsedUrl.query.ref || null;
  const jobIdQuery = parsedUrl.query.jobId || null;
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(getHtmlPage(jobIdQuery, referralCode));
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[SwipeJobs] Web Server & Deep Link Gateway listening on port ${PORT}`);
});
