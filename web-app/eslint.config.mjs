import withNuxt from './.nuxt/eslint.config.mjs'
import eslintConfigPrettier from 'eslint-config-prettier/flat'

export default withNuxt(
  {
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
  {
    ignores: ['.nuxt/**', '.output/**', 'node_modules/**'],
  },
  eslintConfigPrettier
)
