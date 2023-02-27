import Card from "react-bootstrap/Card";
import Stack from "react-bootstrap/esm/Stack";

export default function CredentialCard({ credential }) {
  return (
    <Card>
      <Card.Title>{credential.id}</Card.Title>
      <Card.Text>
        <Stack gap={1}>
          <span>Nickname: {credential.nickName}</span>
          <span>Reg time: {credential.registrationTime}</span>
          <span>Last used: {credential.lastUsedTime}</span>
        </Stack>
      </Card.Text>
    </Card>
  );
}
