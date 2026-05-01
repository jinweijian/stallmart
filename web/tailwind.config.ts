import type { Config } from 'tailwindcss'

export default {
  content: [
    './app/**/*.{vue,ts}',
    './server/**/*.{ts,js}',
  ],
  theme: {
    extend: {
      colors: {
        ink: {
          50: '#f5f7f5',
          100: '#e8efeb',
          200: '#d8e4df',
          300: '#b9c9c4',
          500: '#5d6f6a',
          700: '#2d3d39',
          900: '#17201f',
        },
        brand: {
          50: '#edf7f2',
          100: '#d8efe6',
          600: '#0f6b52',
          700: '#0b5b46',
          800: '#24413a',
        },
        warn: {
          50: '#fff3df',
          700: '#9a5a00',
        },
        danger: {
          50: '#fff0eb',
          600: '#b13d28',
          700: '#9f321f',
        },
      },
      boxShadow: {
        panel: '0 16px 40px rgba(23, 32, 31, 0.06)',
      },
    },
  },
  plugins: [],
} satisfies Config
