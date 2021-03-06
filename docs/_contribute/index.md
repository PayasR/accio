---
layout: contribute
title: Contributing to Accio
---

Accio is an open source project, and warmly welcomes contributions!
The source code is available on [GitHub](https://github.com/privamov/accio).

## Submitting a patch

Anybody can submit a patch, which will be reviewed by a core contributor.
All contributions should be submitted through GitHub pull requests.
If you are not familiar with pull requests, GitHub has [a guide about that](https://help.github.com/articles/about-pull-requests/).
We welcome code contributions (i.e., fixing bugs or adding new features) as well as documentation contributions (i.e., improving an existing page or adding a new topic).

1. If you envision adding a non-trivial new feature, please open an issue first describing what you want to do.
Describe your plan and design, and wait for the agreement of a core contributor.
2. Fork our GitHub repository and implement your changes.
3. Create a pull request describing clearly your changes and the rationale behind it.
4. Answer and address the reviewer's comments.
5. A core contributor will eventually merge your contribution.

## Contributing code

If this is your first contribution, you may want learn [how to compile Accio](compile.html) and have a look at [the code organisation](codebase.html).
Before submitting your patch, make sure that all the tests pass.
If you developed a new feature, add new units tests to prove that your code behaves properly, and to prevent from future regressions.
Fixing a bug also generally requires adding a new unit test, to make sure this thing does not break again.

When touching the Thrift IDL, take extra care not to introduce any backwards-incompatible changes.
If you are required to do so, it should be justified, and will only be included in the next minor release.
In that case you should also provide, whenever possible, an upgrade path for existing users.

## Contributing documentation

The documentation is generated by [Jekyll](https://jekyllrb.com/) and published on GitHub pages.
You need to have Ruby installed on your computer if you want to preview your changes locally (which is recommended).
Then, open a shell and move to the `docs/` directory.
The first time, you will need to install the relevant Ruby gems:
```bash
bundle install
```

Next, you can start Jekyll:
```bash
bundle exec jekyll serve
```

After a few seconds, you should have a local version of the documentation accessible at [http://127.0.0.1:4000/accio/](http://127.0.0.1:4000/accio/).
Each modification done locally will be immediately picked up by Jekyll and made available in a few seconds.

Besides reference documentation (e.g., operators, commands), documentation pages should be included directly at the root of one of the main sections (i.e., Documentation, Contribute, Deploy, etc.).
Most documentation pages start with a short paragraph of introduction, explaining the purpose of the page.
The main content is then divided into subsections, delimited by titles.
Titles should start at the second level, the page title being automatically included as a first-level title before the page content.
You may also include sub-titles if required, but should avoid going deeper (i.e., sub-sub-titles).
The main content is often (although it is not always required) preceded by a table of contents.
Table of contents can be automatically generated with the following macro:

```
* TOC
{:toc}
```

## Support

We welcome any question (technical or not) to be asked as [a GitHub issue](https://github.com/privamov/accio/issues/new?labels=kind/question).

## Licensing

By contributing to Accio, you accept to place your work under the General Public License, Version 3.0.
