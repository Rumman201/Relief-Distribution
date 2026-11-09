---
name: technical-documentation
description: Documentation standards, Architecture Decision Records (ADRs), developer onboarding, phase handoffs, and operational guides for Relief Distribution App.
---

# Technical Documentation Skill

## 1. Purpose
Maintain rigorous, up-to-date, transparent, and actionable engineering documentation, architecture diagrams, decision records, and onboarding guides across all phases of the project lifecycle.

## 2. Activation Criteria
Activate this skill when:
- Authoring or updating project documentation under `docs/upgrade/**`.
- Writing Architecture Decision Records (ADRs).
- Formulating phase completion reports and multi-agent handoffs.
- Documenting system interfaces, API schemas, and onboarding instructions.

## 3. Project Context
- **Baseline Documentation State**: Zero project documentation. No `README.md`, no architecture overview, no setup guide, no API or database documentation.
- **Documentation Center**: `docs/upgrade/` established as the persistent engineering command center across all 10 phases.

## 4. Specialist Engineering Standards
- **Evidence-Based Documentation**: Every claim, metric, or problem statement must link to verified code locations, command outputs, or measured baselines.
- **Architecture as Code**: Use Mermaid diagrams for architecture, sequence flows, and entity relations.
- **Traceability**: All roadmap items must trace directly to numbered findings in the engineering audit.
- **Standardized ADR Format**:
  - Context & Problem Statement
  - Decision Drivers
  - Considered Options & Trade-offs
  - Decision Outcome & Rationale
  - Pros & Cons / Consequences

## 5. Step-by-Step Execution Procedure
1. **Inventory Generation**: Document all modules, classes, resources, and dependencies in `01-project-inventory.md`.
2. **Architecture Mapping**: Map components, trust boundaries, and data flows in `02-architecture-assessment.md`.
3. **Audit Documentation**: Record all specialist findings with severity, impact, and remediation in `03-engineering-audit.md`.
4. **Baseline Metrics**: Log exact commands, test results, and APK sizes in `04-baseline-metrics.md`.
5. **Roadmap & Decisions**: Maintain the master 10-phase plan in `05-master-roadmap.md` and ADRs in `08-architecture-decisions.md`.

## 6. Decision Tree for Ambiguous Cases
- *Where should quick-reference developer instructions go?*
  -> In the root `README.md` and `AGENTS.md`. Detailed historical and architectural rationale belongs in `docs/upgrade/`.

## 7. Tool Instructions
- Use `write_file` and `edit_file` with GitHub-flavored markdown.
- Verify file links using `file:///` format or clean relative paths.

## 8. Safety Constraints
- Never include active production secrets, real user PII, or cloud credentials in documentation.
- Never document untested commands or fabricate performance claims.

## 9. Verification Steps
- Verify all links to repository files and line numbers are valid.
- Verify that markdown renders properly without broken tables or broken Mermaid blocks.

## 10. Expected Output & Evidence Format
- Complete, structured markdown artifacts adhering to the Project Upgrade Control Center specification.

