// frontend/src/App.jsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider }                            from './context/AuthContext';
import { Navbar, ProtectedRoute }                  from './components/layout';

import LandingPage       from './pages/LandingPage';
import LoginPage         from './pages/LoginPage';
import SignupPage        from './pages/SignupPage';
import Dashboard         from './pages/Dashboard';
import RoadmapPage       from './pages/RoadmapPage';
import InterviewSetupPage from './pages/InterviewSetupPage';
import InterviewPage     from './pages/InterviewPage';
import FeedbackPage      from './pages/FeedbackPage';
import HistoryPage       from './pages/HistoryPage';
import ResumeAnalysisPage from './pages/ResumeAnalysisPage';
import ResumeMatchPage    from './pages/ResumeMatchPage';
import ResumeReadinessPage from './pages/ResumeReadinessPage';
import VoiceInterviewPage from './pages/VoiceInterviewPage';
import CompanyPreparationPage from './pages/CompanyPreparationPage';
import CompanyReadinessPage from './pages/CompanyReadinessPage';
import CodingAssessmentPage from './pages/CodingAssessmentPage';
import AptitudePracticePage from './pages/AptitudePracticePage';
import ShareableCardPage from './pages/ShareableCardPage';
import RecruiterSetupPage from './pages/RecruiterSetupPage';
import RecruiterModePage from './pages/RecruiterModePage';

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <Routes>
          {/* Public */}
          <Route path="/"       element={<LandingPage />}   />
          <Route path="/login"  element={<LoginPage />}     />
          <Route path="/signup" element={<SignupPage />}    />

          {/* Protected */}
          <Route path="/dashboard" element={
            <ProtectedRoute><Dashboard /></ProtectedRoute>
          }/>
          <Route path="/roadmap" element={
            <ProtectedRoute><RoadmapPage /></ProtectedRoute>
          }/>
          <Route path="/interview/setup" element={
            <ProtectedRoute><InterviewSetupPage /></ProtectedRoute>
          }/>
          <Route path="/interview" element={
            <ProtectedRoute><InterviewPage /></ProtectedRoute>
          }/>
          <Route path="/feedback" element={
            <ProtectedRoute><FeedbackPage /></ProtectedRoute>
          }/>
          <Route path="/history" element={
            <ProtectedRoute><HistoryPage /></ProtectedRoute>
          }/>
          <Route path="/resume-analysis" element={
            <ProtectedRoute><ResumeAnalysisPage /></ProtectedRoute>
          }/>
          <Route path="/resume-match" element={
            <ProtectedRoute><ResumeMatchPage /></ProtectedRoute>
          }/>
          <Route path="/resume-readiness" element={
            <ProtectedRoute><ResumeReadinessPage /></ProtectedRoute>
          }/>
          <Route path="/voice-interview" element={
            <ProtectedRoute><VoiceInterviewPage /></ProtectedRoute>
          }/>
          <Route path="/company/preparation" element={
            <ProtectedRoute><CompanyPreparationPage /></ProtectedRoute>
          }/>
          <Route path="/company/readiness" element={
            <ProtectedRoute><CompanyReadinessPage /></ProtectedRoute>
          }/>
          <Route path="/company/aptitude" element={
            <ProtectedRoute><AptitudePracticePage /></ProtectedRoute>
          }/>
          <Route path="/company/coding" element={
            <ProtectedRoute><CodingAssessmentPage /></ProtectedRoute>
          }/>
          <Route path="/share-card" element={<ShareableCardPage />} />
          
          {/* Recruiter Mode */}
          <Route path="/recruiter/setup" element={
            <ProtectedRoute><RecruiterSetupPage /></ProtectedRoute>
          }/>
          <Route path="/recruiter/interview" element={
            <ProtectedRoute><RecruiterModePage /></ProtectedRoute>
          }/>
        </Routes>
      </Router>
    </AuthProvider>
  );
}