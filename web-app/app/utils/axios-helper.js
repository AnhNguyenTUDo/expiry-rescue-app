export async function requestAxios(promise, errorCallBack) {
  try {
    const response = await promise

    return response.data
  } catch (error) {
    if (errorCallBack) {
      errorCallBack(error)
    }
    console.error(error)
  }
}
