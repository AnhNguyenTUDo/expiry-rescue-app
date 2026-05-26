import { defineConfig } from "eslint/config";
import eslintConfigPrettier from "eslint-config-prettier/flat";

export default defineConfig([
  {
    rules: {
      "no-unused-vars": "warn",
      "no-console": "warn",
      "vue/html-self-closing": "off",
      "vue/multi-word-component-names": "off",
    },
  },
  eslintConfigPrettier,
]);
