package com.roxstar.audio.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// ROXSTAR Glassmorphic Theme Palette
// Calibrated from Reference Design
// ==========================================

// Core Accent & Brand Colors
val RoxstarPrimary = Color(0xFF6366F1)      // Electric Indigo
val RoxstarAccent = Color(0xFFEC4899)       // Vibrant Neon Rose
val RoxstarViolet = Color(0xFFA855F7)       // Radiant Purple
val RoxstarCyan = Color(0xFF06B6D4)         // Cyber Cyan
val RoxstarAmber = Color(0xFFF59E0B)        // Warm Sunset Amber
val RoxstarSuccess = Color(0xFF10B981)      // Emerald Green
val RoxstarDanger = Color(0xFFEF4444)       // Crimson Red

// Dark Mode Tokens (Deep Atmospheric Obsidian)
val DarkCanvas = Color(0xFF0C0D14)
val DarkCanvasSecondary = Color(0xFF131520)
val DarkGlassSurface = Color(0xB8171927)       // ~72% alpha frosted glass
val DarkGlassSurfaceLight = Color(0xD0212438)  // Highlighted glass surface
val DarkGlassBorder = Color(0x29FFFFFF)        // 16% white specular border
val DarkGlassBorderHighlight = Color(0x4DFFFFFF) // 30% white specular highlight
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFFCBD5E1)
val DarkTextMuted = Color(0xFF94A3B8)

// Light Mode Tokens (Frosted Porcelain & Crystal)
val LightCanvas = Color(0xFFF1F4F9)
val LightCanvasSecondary = Color(0xFFE2E8F0)
val LightGlassSurface = Color(0xD9FFFFFF)      // 85% alpha white frosted glass
val LightGlassSurfaceHighlight = Color(0xF2FFFFFF)
val LightGlassBorder = Color(0x66FFFFFF)       // Crisp white rim reflection
val LightGlassBorderSubtle = Color(0x1A0F172A) // 10% dark rim for light mode depth
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF334155)
val LightTextMuted = Color(0xFF64748B)

// Ambient Glow Orb Colors (for atmospheric background mesh)
val AmbientGlowRose = Color(0x35F43F5E)
val AmbientGlowViolet = Color(0x308B5CF6)
val AmbientGlowAmber = Color(0x28F59E0B)
val AmbientGlowCyan = Color(0x2506B6D4)

// Legacy alias compatibility
val DarkBackground = DarkCanvas
val CardBackground = DarkGlassSurface
val CardBorder = DarkGlassBorder
val Primary = RoxstarPrimary
val Accent = RoxstarAccent
val Success = RoxstarSuccess
val Warning = RoxstarAmber
val Danger = RoxstarDanger
val TextPrimary = DarkTextPrimary
val TextMuted = DarkTextMuted
