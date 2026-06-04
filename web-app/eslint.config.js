import { defineConfig } from 'eslint/config'
import eslintConfigPrettier from 'eslint-config-prettier/flat'
import importPlugin from 'eslint-plugin-import'

export default defineConfig([
  {
    ignores: ['.nuxt/**', '.output/**', 'node_modules/**'],
  },
  {
    plugins: {
      import: importPlugin,
    },
    rules: {
      'no-unused-vars': 'warn',
      'no-console': ['warn', { allow: ['error', 'warn'] }],
      'vue/html-self-closing': 'off',
      'vue/multi-word-component-names': 'off',
      'sort-imports': [
        'error',
        {
          ignoreCase: false,
          ignoreDeclarationSort: true,
          ignoreMemberSort: false,
          memberSyntaxSortOrder: ['multiple', 'single', 'all', 'none'],
          allowSeparatedGroups: false,
        },
      ],
      'import/order': [
        'error',
        {
          groups: ['external', 'builtin', 'internal', 'parent', 'sibling', 'index'],
          'newlines-between': 'never',
          alphabetize: { order: 'asc', caseInsensitive: true },
        },
      ],
    },
  },
  eslintConfigPrettier,
])
