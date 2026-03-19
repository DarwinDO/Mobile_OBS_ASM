# Android UI With XML Views

## Recommended Building Blocks

- `ConstraintLayout` for flexible screens
- `RecyclerView` for lists
- `MaterialToolbar`
- `TextInputLayout` and `TextInputEditText`
- `MaterialButton`
- `ProgressBar`

## Minimum UX Standard

- Tap targets around 48dp
- Visible loading state
- Visible empty state
- Retry action for failed requests when practical

## List Rules

- Use `RecyclerView`
- Keep adapter classes focused
- Bind only what the item needs
- Avoid expensive work inside `onBindViewHolder`

## Form Rules

- Validate before sending request
- Show field-level error when possible
- Disable submit while request is in flight when appropriate

