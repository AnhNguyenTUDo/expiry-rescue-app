import { defineConfig } from "eslint/config";
import eslintConfigPrettier from "eslint-config-prettier/flat";

export default defineConfig([
  {
    ignores: [".nuxt/**", ".output/**", "node_modules/**"],
  },
  {
    rules: {
      "no-unused-vars": "warn",
      "no-console": ["warn", { allow: ["error", "warn"] }],
      "vue/html-self-closing": "off",
      "vue/multi-word-component-names": "off",
    },
  },
  eslintConfigPrettier,
]);
