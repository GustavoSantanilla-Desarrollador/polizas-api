# 4. Git — incorporar solo el fix de seguridad

La estrategia correcta es identificar el commit específico en `main` y aplicar `cherry-pick`.

```bash
git fetch origin
git switch feature/new-login
git log origin/main --oneline
git cherry-pick <COMMIT_SHA>
git push origin feature/new-login
```

Si hay conflictos:

```bash
git status
# resolver archivos
git add .
git cherry-pick --continue
```

Para abortar:

```bash
git cherry-pick --abort
```

## Por qué

`cherry-pick` aplica el cambio de un commit concreto sin fusionar todo el historial nuevo de `main`.

Antes de hacerlo validaría que el commit sea autosuficiente y que sus dependencias no requieran otros commits de `main`. Si el fix depende de una cadena de commits, seleccionaría esa cadena en orden o coordinaría un backport controlado.
