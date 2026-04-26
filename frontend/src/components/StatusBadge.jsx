// StatusBadge.jsx - renders a colored pill for any status/type value
export default function StatusBadge({ value }) {
  if (!value) return null;
  const key = value.toLowerCase();
  return (
    <span className={`badge badge-${key}`}>
      <span className="badge-dot" />
      {value}
    </span>
  );
}
