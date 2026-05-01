module.exports = {
  plugins: {
    tailwindcss: {},
    autoprefixer: {
      enable: true,
      config: {
        overrideBrowserslist: ['iOS >= 10', 'Android >= 5'],
      },
    },
  },
}
