/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{vue,js,ts,jsx,tsx,html}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#FF6B35',
          50: '#FFF0EB',
          100: '#FFE0D6',
          200: '#FFC2AD',
          300: '#FFA385',
          400: '#FF7F5C',
          500: '#FF6B35',
          600: '#E55A27',
          700: '#B8461A',
          800: '#8A3413',
          900: '#5C220D',
        },
        secondary: {
          DEFAULT: '#2EC4B6',
          50: '#E6FAF8',
          100: '#CCF5F1',
          200: '#99EBE3',
          300: '#66E0D5',
          400: '#33D6C8',
          500: '#2EC4B6',
          600: '#259D91',
          700: '#1C766D',
          800: '#134E49',
          900: '#0A2726',
        },
        background: '#FFF8F0',
        foreground: '#333333',
        surface: '#FFFFFF',
        muted: '#F5F5F5',
        border: '#E5E5E5',
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', '"PingFang SC"', '"Helvetica Neue"', 'Arial', 'sans-serif'],
      },
      spacing: {
        'safe-area-inset-top': 'env(safe-area-inset-top)',
        'safe-area-inset-bottom': 'env(safe-area-inset-bottom)',
      },
      boxShadow: {
        'card': '0 2px 8px rgba(0, 0, 0, 0.08)',
        'elevated': '0 4px 16px rgba(0, 0, 0, 0.12)',
      },
      borderRadius: {
        'xl': '12px',
        '2xl': '16px',
      },
    },
  },
  plugins: [],
  corePlugins: {
    preflight: false,
  },
  important: true,
}
