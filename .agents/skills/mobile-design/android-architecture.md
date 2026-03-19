# Android Architecture Notes

## Default Recommendation For This Project

For the current MVP, use a simple structure:

- `MainActivity` as shell entry point
- fragments for main app sections when needed
- repository classes for API access
- adapters for list rendering

## Suggested Flow

`Activity or Fragment -> ViewModel style class or controller -> Repository -> ApiService -> Backend`

## Keep It Simple

- For a single-screen flow, an activity plus repository is fine.
- Introduce fragments only when navigation starts to branch.
- Introduce view models when state survives rotation or becomes harder to manage in the screen class.

## Avoid

- deep clean-architecture layers for a basic student MVP
- several abstraction layers before there is real complexity

