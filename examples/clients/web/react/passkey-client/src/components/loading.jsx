import Spinner from "react-bootstrap/Spinner";

export default function Loading({ loadingInfo }) {
  return (
    <div style={{ textAlign: "center" }}>
      <Spinner size="lg" />
      <h5>{loadingInfo.message}</h5>
    </div>
  );
}
