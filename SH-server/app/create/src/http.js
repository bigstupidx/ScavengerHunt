export const post = (url, body, cb) => {
    fetch(url, {
        method: 'POST',
        body: JSON.stringify(body),
    })
    .then((response) => response.json())
    .then(cb)
    .catch((error) => {
        console.error(error);
    })
};
