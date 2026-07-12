# Guia de Versionamento e Lançamento (Release)

Este projeto adota o **Semantic Versioning 2.0.0** (SemVer) e realiza o deploy automatizado através de tags do Git. A compilação e publicação da release só ocorrem se a tag do Git corresponder exatamente à versão configurada no aplicativo.

---

## 1. Padrão de Versão (SemVer)

O formato da versão deve ser `MAJOR.MINOR.PATCH`:

*   **MAJOR**: Alterações incompatíveis com versões anteriores.
*   **MINOR**: Novas funcionalidades compatíveis com versões anteriores.
*   **PATCH**: Correções de bugs compatíveis com versões anteriores.

---

## 2. Passo a Passo para Lançar uma Nova Versão

Para realizar o deploy de uma nova versão do aplicativo, siga os passos abaixo:

### Passo 2.1: Atualizar a versão no código
No arquivo [app/build.gradle.kts](file:///c:/Users/user/Projetos/opendriver/app/build.gradle.kts), atualize os seguintes campos dentro de `defaultConfig`:

1.  **`versionCode`**: Incremente o valor inteiro em `1`. (Ex: de `1` para `2`).
2.  **`versionName`**: Atualize para a versão semântica desejada. (Ex: `"1.0.1"`).

```kotlin
defaultConfig {
    // ...
    versionCode = 2
    versionName = "1.0.1"
}
```

### Passo 2.2: Commitar as alterações
Crie um commit contendo a alteração do arquivo de configuração:

```bash
git add app/build.gradle.kts
git commit -m "chore: bump version to 1.0.1"
git push origin <sua-branch>
```

### Passo 2.3: Criar e Enviar a Tag
Após fazer o merge para a branch principal (`main`), crie a tag do Git correspondente à versão adicionando o prefixo `v`:

```bash
# Cria a tag localmente
git tag v1.0.1

# Envia a tag para o GitHub
git push origin v1.0.1
```

---

## 3. Validação Automática e Compilação

Ao enviar a tag para o GitHub:
1. O workflow do GitHub Actions será disparado pela tag `v*`.
2. O workflow irá extrair o `versionName` do arquivo `app/build.gradle.kts` e verificar se é idêntico à tag criada (desconsiderando o prefixo `v`).
3. **Se as versões forem idênticas**: A compilação dos APKs (Release e Debug) será iniciada e eles serão publicados automaticamente como assets em uma nova GitHub Release.
4. **Se as versões divergirem**: O processo falhará imediatamente e nenhuma compilação será realizada.
