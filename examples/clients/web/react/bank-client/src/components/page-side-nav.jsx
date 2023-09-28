import { Link } from "react-router-dom";

/**
 * linkObject denoted as
 *  {
 *    name: descriptive title of the link
 *    link: URL or path for when a link is clicked
 *  }
 */
export default function PageSideNav({ title, linkObjects }) {
  console.log(linkObjects);
  return (
    <div>
      <div>
        <h3>{title}</h3>
      </div>
      <div style={{ marginTop: "24px" }}>
        {linkObjects.map((value) => (
          <div>
            <Link className="nav-link-account" to={value.link}>
              {value.name}
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}
