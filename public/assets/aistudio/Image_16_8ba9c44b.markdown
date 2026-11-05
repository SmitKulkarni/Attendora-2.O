---
name: Attendora Modern EdTech
colors:
  surface: '#f4fafe'
  surface-dim: '#d5dbdf'
  surface-bright: '#f4fafe'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eef4f8'
  surface-container: '#e9eff3'
  surface-container-high: '#e3e9ed'
  surface-container-highest: '#dde3e7'
  on-surface: '#161c1f'
  on-surface-variant: '#3e484b'
  inverse-surface: '#2b3134'
  inverse-on-surface: '#ecf2f6'
  outline: '#6e797b'
  outline-variant: '#bec8cb'
  surface-tint: '#006876'
  primary: '#006673'
  on-primary: '#ffffff'
  primary-container: '#1d808f'
  on-primary-container: '#f8feff'
  inverse-primary: '#7ed3e3'
  secondary: '#515f74'
  on-secondary: '#ffffff'
  secondary-container: '#d5e3fc'
  on-secondary-container: '#57657a'
  tertiary: '#006947'
  on-tertiary: '#ffffff'
  tertiary-container: '#00855b'
  on-tertiary-container: '#f5fff6'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#9eefff'
  primary-fixed-dim: '#7ed3e3'
  on-primary-fixed: '#001f24'
  on-primary-fixed-variant: '#004e59'
  secondary-fixed: '#d5e3fc'
  secondary-fixed-dim: '#b9c7df'
  on-secondary-fixed: '#0d1c2e'
  on-secondary-fixed-variant: '#3a485b'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#f4fafe'
  on-background: '#161c1f'
  surface-variant: '#dde3e7'
typography:
  headline-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  stat-metric:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 15px
    fontWeight: '500'
    lineHeight: 22px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 13px
    fontWeight: '600'
    lineHeight: 18px
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1.25rem
  gutter-sm: 0.75rem
  gutter-lg: 1.75rem
  margin: 1.5rem
  margin-sm: 1rem
  margin-lg: 2.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system establishes an intelligent, reassuring, and streamlined workspace tailored for educators, school administrators, and academic staff. Built to simplify high-frequency attendance tracking and student management workflows, the visual language balances high-density utility with soothing clarity.

The aesthetic blends **Modern Corporate SaaS** with subtle **Soft Glassmorphism**. Layouts prioritize clarity, soft ambient depth, and non-distracting visual structures that reduce cognitive fatigue during daily classroom check-ins. White cards float over soft ice-slate backgrounds with gentle inner shadows, delicate hairline borders, and translucent nav components. Visual cues are crisp and immediate, utilizing distinct semantic status indicators (emerald green, coral red, warm amber) alongside an authoritative ocean-teal core.

## Colors

The palette is engineered around clean clinical contrast, high accessibility, and clear administrative hierarchy:

- **Primary (`#2E8B9A`)**: A deep ocean teal providing grounded authority for key navigation states, active menu items, primary action buttons, and analytical line trends.
- **Secondary (`#475569`)**: A balanced slate-gray serving secondary UI icons, tabular subtitles, secondary headers, and structural borders.
- **Tertiary (`#10B981`)**: A vibrant emerald green dedicated to positive confirmation, cloud synchronization states, and active "Present" student badges.
- **Neutral (`#EBF1F5`)**: An ice-tinted slate neutral used across app canvas backgrounds, creating soft relief under crisp white (`#FFFFFF`) surface cards.

### Semantic Badge Tones
- **Present**: Emerald Soft Fill (`#E6F8F0`), Emerald Stroke (`#34D399`), Emerald Text (`#065F46`).
- **Absent**: Coral Soft Fill (`#FEECEB`), Coral Stroke (`#F87171`), Coral Text (`#991B1B`).
- **Late**: Amber Soft Fill (`#FEF7E6`), Amber Stroke (`#FBBF24`), Amber Text (`#92400E`).

## Typography

The design system standardizes on **Plus Jakarta Sans** across all roles to achieve an inviting, contemporary, and geometric aesthetic suited for digital dashboards.

- **Numerics & Key Stats**: Large tabular numbers with medium-to-bold weights ensure instantaneous scanning across classroom metric cards.
- **Table Data**: Set in 13px–14px with medium weight for names and regular weights for secondary parameters (Roll No, Class, Time), preserving scannability on dense lists.
- **Headings**: Clean geometric weights with tight vertical line-height prevent excess vertical bulk in headers and widget wrappers.

## Layout & Spacing

Layouts follow a fluid, responsive 12-column grid system bounded inside standard responsive viewports:

- **Desktop (1200px+)**: Sidebar navigation fixed at 240px width; main dashboard body spans remaining width utilizing fluid column tracks with `1.25rem` gutters and `1.5rem` margins.
- **Tablet (768px - 1199px)**: Sidebar folds into an icon rail (72px) or collapsible glass drawer; metric cards wrap into a 2x2 grid.
- **Mobile (< 768px)**: Single-column stack with horizontal overflow scroll for multi-column tables, outer canvas padding constrained to `1rem`.

Component internal padding obeys an 8pt modular scale: cards employ `space-lg` (24px) for major widgets and `space-md` (16px) for compact summary blocks.

## Elevation & Depth

Visual depth is achieved through layered ambient shadows, subtle surface tints, and crisp ghost borders rather than harsh drop shadows.

- **Base Canvas**: Flat slate-tinted canvas `#EBF1F5` that recedes naturally.
- **Standard Card Container**: Solid `#FFFFFF` background equipped with a gentle boundary outline `1px solid rgba(226, 232, 240, 0.8)` and a diffused dual-tier shadow:
  `box-shadow: 0 1px 3px rgba(15, 23, 42, 0.03), 0 8px 16px -4px rgba(15, 23, 42, 0.04);`
- **Interactive Glass Surfaces**: Search fields, utility pills, and popovers utilize `rgba(255, 255, 255, 0.75)` with `backdrop-filter: blur(12px)` and a subtle `rgba(255, 255, 255, 0.6)` top edge highlight.
- **Hover Transitions**: Elevated hover states add a slight lift (`translateY(-2px)`) while enriching the ambient spread shadow.

## Shapes

The design system employs a **Rounded** shape language (`roundedness: 2`):

- **Cards & Data Containers**: `1rem` (16px) corner radiuses offer friendly structure without feeling playful.
- **Interactive Inputs & Search Bars**: `0.75rem` (12px) to `1.5rem` (capsule format for top search) maintaining soft tactile handoffs.
- **Status Badges & Indicators**: Fully pill-shaped (`9999px`) rounded corners to reinforce quick tag recognition.
- **Avatars**: Pure circular geometry (`50%` radius) balancing rectangular data rows.

## Components

### Status Pill Badges
- **Present**: Capsule badge with `#E6F8F0` fill, `#34D399` stroke (1px), `#065F46` label.
- **Absent**: Capsule badge with `#FEECEB` fill, `#F87171` stroke (1px), `#991B1B` label.
- **Late**: Capsule badge with `#FEF7E6` fill, `#FBBF24` stroke (1px), `#92400E` label.
- Dimensions: `padding: 4px 14px`, `font-size: 13px`, `font-weight: 600`.

### Data Cards & Metric Summaries
- Pure white background cards with soft `1px` border borders.
- Top-level metric cards utilize subtle tinted header backgrounds or soft status fills (e.g. subtle green tint for on-time cards, soft peach for absent summaries).
- Large numerical readouts paired with small muted label captions.

### Interactive Data Tables
- Header row styled with muted text (`#64748B`), uppercase tracking (`label-sm`), and a soft background track (`#F8FAFC`).
- Student rows include avatar image, text stack (name + ID), and zebra-hover transition (`#F8FAFC`).
- Bottom row dividing borders use hairline `#F1F5F9`.

### Input & Search Fields
- Semi-translucent glass input containers with search icon prefix.
- Active states feature a focused ring: `0 0 0 3px rgba(46, 139, 154, 0.15)` and border transition to primary teal.

### Navigation Rails
- Clean sidebar vertical items: active tab styled with solid primary `#2E8B9A` background, white icon and label, rounded `0.75rem` profile.
- Inactive items styled with `#64748B` slate icons and labels that shift to `#334155` on hover.